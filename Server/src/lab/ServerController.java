package lab;

import lab.BasicClasses.MusicBand;
import lab.Commands.Command;
import lab.Commands.ConcreteCommands.Auth;
import lab.Commands.ConcreteCommands.Register;
import lab.Commands.SerializedCommands.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerController implements Runnable {

	private static final int BUFF_SIZE = 1000000;
	static final String host = "127.0.0.1";
	static int port = 1010;
	private static final List<Message> incomingMessages = Collections.synchronizedList(new LinkedList<>());
	private static final List<Message> outcomingMessages = Collections.synchronizedList(new LinkedList<>());
	public static boolean filter = false;
	public static ArrayList<MusicBand> filterArray;

	static Iterator<SelectionKey> iterator;
	static Selector selector;
	static ServerSocketChannel server;


	ServerController(String port){
		ServerController.port = Integer.parseInt(port);
	}

	public void run() {
		//noinspection ResultOfMethodCallIgnored
		Runtime.getRuntime().addShutdownHook(new Thread(BD::save));
		try {
			selector = Selector.open();
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(host, port));
			server.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("Server started");

			StringBuilder console_line = new StringBuilder();
			//noinspection InfiniteLoopStatement
			while (true) {
				if (System.in.available() > 0) {
					int ch;
					while (true){
						ch = System.in.read();
						if(ch == 10){
							break;
						}
						else {
							console_line.append((char) ch);
						}
					}
					server_commands(console_line.toString());
					console_line = new StringBuilder();
				}
				selector.select();
				iterator = selector.selectedKeys().iterator();



				ExecutorService executorService = Executors.newFixedThreadPool(9);
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					if (selector.isOpen()) {
						Thread reader = new Thread(new Reader(key));
						reader.start();
						executorService.submit(new Connector(key));
						executorService.submit(new CommandExecute(key));
						reader.join();
						executorService.submit(new Writer(key));
					}
				}


			}
		} catch (SocketException e){
			System.out.println("Порт занят");
			System.exit(0);
		} catch(Exception e){
			System.out.println("Приложение завершено");
			e.printStackTrace();
		}
	}


	synchronized public static Message getSocketObject(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
		ByteBuffer data = ByteBuffer.allocate(BUFF_SIZE);
		socketChannel.read(data);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array());
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		Message message = (Message)objectInputStream.readObject();
		message.setSocketChannel(socketChannel);
		return message;
	}

	synchronized private static void sendSocketObject(SocketChannel client, Message message) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(message);
		objectOutputStream.flush();
		client.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
	}

	private static void server_commands(String s){
		if(s.equals("save")){
			if (BD.save()) {
				System.out.println("Успешное сохранение");
			}else{
				System.out.println("Сохранение не удалось");
			}
		}
		else if(s.equals("end")){
			System.out.println("Выход из программы");
			if (!BD.save()) {
				System.out.println("Сохранение не удалось");
			}
			System.exit(0);
		}
	}
	static class Writer implements Runnable{
		SelectionKey key;
		Writer(SelectionKey key){
			this.key = key;
		}
		@Override
		public void run() {
			if(key.isValid() && key.isWritable()) {
				SocketChannel channel = (SocketChannel) key.channel();
				synchronized (outcomingMessages) {
					for (Message mes : outcomingMessages) {
						if (mes.getSocketChannel().equals(channel)) {
							try {
								sendSocketObject(channel, mes);
								outcomingMessages.remove(mes);
								channel.register(selector, SelectionKey.OP_READ);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	static class CommandExecute implements Runnable{
		SelectionKey key;
		public CommandExecute(SelectionKey key) {
			this.key = key;
		}

		@Override
		public void run() {
			String result = "Неизвестная команда";
			synchronized (incomingMessages) {
				for (Message mes : incomingMessages) {
					if (BD.checkPass(mes.getUser_name(), mes.getPass()) || (mes.getCommand().getClass() == Auth.class) || (mes.getCommand().getClass() == Register.class)) {
						Command command = mes.getCommand();
						try {
							result = command.execute(mes);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Message message = new Message(result);
					message.setArraylist(BD.getData());
					if (filter) {
						message.setArraylist(filterArray);
						filter = false;
					}
					message.setSocketChannel(mes.getSocketChannel());
					synchronized (incomingMessages) {
						incomingMessages.remove(mes);
					}
					synchronized (outcomingMessages) {
						outcomingMessages.add(message);
					}
				}
			}
		}
	}

	static class Reader implements Runnable {
		SelectionKey key;
		Reader(SelectionKey key){
			this.key = key;
		}
		@Override
		public void run() {
			if (key.isValid() && key.isReadable()) {
				SocketChannel channel = (SocketChannel) key.channel();
				try {
					synchronized (incomingMessages) {
						Message message = getSocketObject(channel);
						message.setSocketChannel(channel);
						incomingMessages.add(message);
						channel.register(selector, SelectionKey.OP_WRITE);
					}
				} catch (IOException | ClassNotFoundException e) {
					System.out.println("Клиент отключился");
					try {
						channel.close();
						server.register(selector, SelectionKey.OP_ACCEPT);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			}
		}
	}
	static class Connector implements Runnable{
		SelectionKey key;
		Connector(SelectionKey key){
			this.key = key;
		}
		@Override
		public void run() {
			if(key.isValid() && key.isAcceptable()) {
				try {
					SocketChannel client = server.accept();
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					System.out.println("Новое подключение");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}