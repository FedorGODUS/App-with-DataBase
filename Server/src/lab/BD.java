package lab;



import lab.BasicClasses.*;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Класс - база данных, позволяет проводить операции с базой данных. Без него коллекция не будет работать.
 * @autor Цветков Фёдор Николаевич P3132
 * @version 1.1
 */
public class BD {

    /** Поле, которое хранит базу данных */
    static Connection connection = null;

    /** Колекция, которая используется для представления данных в работающей программе. */
    private static ArrayList<MusicBand> data = new ArrayList<>();
    private static ArrayList<User> users = new ArrayList<>();


    public static boolean reverse = false;

    private static BD bd = null;

    /**
     * Создание базы данных, загрузка данных из прошлой сессии или же создание новой в случае отсутствие прошлых сессий.
     *
     */
//    public BD(String file_path) {
//        BD.file_path = file_path;
//        if(load()){
//            System.out.println("Загрузка базы данных успешна");
//        }
//        else{
//            System.out.println("Создана пустая коллекция");
//            if(save()){
//                System.out.println("Файл создан");
//            }
//            else{
//                System.out.println("Нет доступа к файлу");
//                System.exit(0);
//            }
//        }
//    }
    public BD(String db_url, String user, String pass) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection(db_url, user, pass);
            System.out.println("Connection Successful");
            if (createTable()){
                System.out.println("Creation Successful");
                if (load()){
                    System.out.println("Load Successful");
                }
                else{
                    System.out.println("Load Failed");
                    System.exit(0);
                }
            }
            else{
                System.out.println("Creation Failed");
                System.exit(0);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            System.exit(0);
        }
    }



    /** Метод, позволяет получить id для нового объекта.
     *
     * @return возвращает int ID
     * */
    public static int giveID(){
        boolean is = false;
        for(int result = 0; result < data.size(); result++){
            for(MusicBand m : data){
                if(m.getID() == result){
                    is = true;
                }
            }
            if(is){
                is = false;
            }
            else{
                return result;
            }
        }
        return data.size();
    }
    /** Метод, позволяющий подметить какой-либо объект по ID.
     * @param id ID объекта, который мы хотим поменять.
     * @param musicBand объект.
     *
     * @return возвращает успешность выполнения метода. true - успех, false - исключение
     * */
    public static boolean update(MusicBand musicBand, Integer id){
        try{
            //TODO проверить
            data.set(id, musicBand);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    /** Метод позволяет сохранить коллекцию в файл, название файла указывалось присоздании объекта.
     *
     * @return возвращает успешность выполнения метода. true - успех, false - исключение
     * */
    public static boolean save(){
        //TODO save

        return true;

    }

    public static String addIfMax(MusicBand musicBand) {
        if (!data.isEmpty()) {
            int count = 0;
            for (MusicBand mb : data) {
                if (mb.compareTo(musicBand) < 0) {
                    count = count + 1;
                }
            }
            if (count == data.size()) {
                BD.add(musicBand);

            }
            return "done";
        } else {
            data.add(musicBand);
            return "Коллекция пуста";
        }

    }



    /** Метод, позволяет загрузить коллекцию из файла.
     *
     * @return возвращает успешность выполнения метода. true - успех, false - исключение
     * */
    private boolean load(){
        //TODO load
        try {
            BufferedReader br = new BufferedReader(new FileReader(file_path));
            String line;
            while ((line = br.readLine()) != null){
                String[] band = line.split(",");
                MusicBand b = new MusicBand(Long.parseLong(band[0]),band[1],new Coordinates(Long.parseLong(band[2]),Long.parseLong(band[3])), LocalDateTime.now(),Integer.parseInt(band[4]),band[5], MusicGenre.valueOf(band[6]),new Label(Integer.parseInt(band[7])),Integer.parseInt(band[8]));
                data.add(b);
            }
            return true;
        } catch (IOException e){
            return false;
        }


    }
    /** Метод, позволяет добавить объект в коллекцию.
     *
     * @param musicBand Объект.
     *
     * @return возвращает успешность выполнения метода. true - успех, false - исключение
     * */
    public static boolean add(MusicBand musicBand){
        try {








            String id = String.valueOf(giveID());
            String name = musicBand.getName();
            String description = musicBand.getDescription();
            String x =String.valueOf(musicBand.getCoordinates().getX());
            String y =String.valueOf(musicBand.getCoordinates().getY());
            String numberOfParticipants=String.valueOf(musicBand.getNumberOfParticipants());
            String albumCount=String.valueOf(musicBand.getAlbumCount());
            String genre =String.valueOf(musicBand.getGenre());
            String sales=String.valueOf(musicBand.getLabel().getSales());
            Date creation_date = Date.valueOf(musicBand.getCreationDate().toLocalDate());
            String user_creator= musicBand.getUser_creator();




            PreparedStatement preparedStatement =connection.prepareStatement();






            data.add(musicBand);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /** Метод, позволяет удалять объекты из коллекции по ID.
     * ВАЖНО: ID в коллекции начинаються с 1, а не с 0.
     * @param id ID файла, который хотим удалить.
     *
     * @return возвращает успешность выполнения метода. true - успех, false - исключение.
     * */
    public static boolean remove(int id) {
        try {
            Statement stmt = connection.createStatement();
            String sql = String.format("DELETE from DATA_BD where ID=%d;", id);
            stmt.executeUpdate(sql);
            data.removeIf(m -> m.getID() == id);
            return true;
        }
        catch (Exception ignored){}
        return false;
    }
    /** Метод, позволяет очищать коллекцию.
     *
     * @return возвращает успешность выполнения метода. true - успех, false - исключение.
     * */
    public static boolean clean(){
        try {
            data = new ArrayList<>();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    /** Метод, позволяет получить дату создания файла.
     *
     * @return возвращает String - дата создания.
     * */
    public static String getCreateTime(){
        try {
            //return data.get(0).getCreateTime().toString();
//            BasicFileAttributes attr = Files.readAttributes(Paths.get(file_path), BasicFileAttributes.class);
            return LocalDateTime.now().toString();
        }
        catch (Exception e){
            return "В коллекции нет элементов.";
        }
    }
    /** Метод, позволяет получить объект по его ID.
     * ВАЖНО: ID в коллекции начинаються с 1, а не с 0.
     *
     * @param id ID объекта.
     *
     * @return Объект MusicBand.
     * */
    public static MusicBand get(int id){
        return data.get(id);
    }
    /** Метод, позволяет получить количество элементов в коллекции.
     *
     * @return int - колличество элементов.
     * */
    public static int size(){return data.size();}

    /** Метод, позволяет отсортировать массив по текущему методу сортировки.*/
    public static void sort(){
        if(!BD.reverse){
            data.sort(Comparator.comparingLong(MusicBand::getID));
        }
        else{
            data.sort((player2, player1) -> Long.compare(player1.getID(), player2.getID()));
        }
    }

    public static boolean checkExist(Integer groupId) {
        for (MusicBand musicBand:data) {
            if (musicBand.getID() == groupId) {
                return true;
            }
        }
        return false;
    }

    public static String head() {

        if (!data.isEmpty()){
            return data.get(data.size()-1).toString();
        }else {
            return "Коллекция пуста";
        }


    }

    public static String sha1(String input) {
        String sha1 = null;
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(input.getBytes("UTF-8"), 0, input.length());
            sha1 = DatatypeConverter.printHexBinary(msdDigest.digest());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ignored) {}
        return sha1;
    }


    public static String removeHead(){
        if (!data.isEmpty()){
            data.remove(data.get(data.size()-1));
            return "head удалён";
        }
        else {
            return "Коллекция пуста";
        }
    }

    public static String averageOfAlbumCount(){
        if (!data.isEmpty()){
            int count=0;
            for (MusicBand mb: data) {
                count=count+mb.getAlbumCount();
            }
            return String.valueOf(count/data.size());
        }
        else {
            return "Коллекция пуста";
        }
    }

    public static ArrayList<MusicBand> getData() {
        return data;
    }




















    public static int findUser(String name, String pass){
        User result = null;
        for(User user : users){
            if(user.getName().equals(name)){
                result = user;
                break;
            }
        }
        if(result != null){
            if(result.getPass().equals(sha1(pass))){
                System.out.println("Пользователь авторизовался");
                return 1;//успешная авторизация
            }
            else {
                System.out.println("Пользователь не смог авторизоваться");
                return -1;//Неправильный пароль
            }
        }
        else {
            users.add(new User(name, sha1(pass)));
            System.out.println("Пользователь зарегистрирован");
            return 0;// Успешная регистрация
        }
    }



}
