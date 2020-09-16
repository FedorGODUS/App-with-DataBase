package lab;



import lab.BasicClasses.*;

import javax.jws.soap.SOAPBinding;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public static long giveID(){
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
            String name = musicBand.getName();
            String x = String.valueOf(musicBand.getCoordinates().getX());
            String y = String.valueOf(musicBand.getCoordinates().getY());
            Date creationDate = Date.valueOf(musicBand.getCreationDate().toLocalDate());
            int numberOfParticipants = musicBand.getNumberOfParticipants();
            int albumCount =musicBand.getAlbumCount();
            String description = musicBand.getDescription();
            String genre = String.valueOf(musicBand.getGenre());
            int sales = musicBand.getLabel().getSales();

            String sql = "UPDATE DATA_BD "
                    + "SET "
                    + "NAME = ?, "
                    + "X = ?, "
                    + "Y = ?, "
                    + "CREATE_DATE = ?,"
                    + "NUMBER_OF_PARTICIPANTS = ?,"
                    + "DESCRIPTION = ?,"
                    + "GENRE = ?,"
                    + "SALES = ?,"
                    + "ALBUM_COUNT = ? "
                    + "WHERE ID=?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            statement.setInt(2,Integer.parseInt(x));
            statement.setInt(3,Integer.parseInt(y));
            statement.setDate(4,creationDate);
            statement.setInt(5,numberOfParticipants);
            statement.setString(6,description);
            statement.setString(7,genre);
            statement.setInt(8,sales);
            statement.setInt(9,albumCount);
            statement.setInt(10,id);
            int numberOfUpdatedRows = statement.executeUpdate();
            //connection.commit();
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
            BD.add(musicBand);
            return "Коллекция пуста";
        }

    }



    /** Метод, позволяет загрузить коллекцию из файла.
     *
     * @return возвращает успешность выполнения метода. true - успех, false - исключение
     * */
    private boolean load(){
        //TODO load


        String sql;
        ResultSet resultSet;
        try {
            sql=" SELECT * FROM BASE";
            resultSet= connection.createStatement().executeQuery(sql);
            while (resultSet.next()){
                Long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                Long x =resultSet.getLong("X");
                Long y = resultSet.getLong("Y");
                Date creation_date = resultSet.getDate("CREATE_DATE");
                LocalDateTime ldt = Instant.ofEpochMilli( creation_date.getTime() )
                        .atZone( ZoneId.systemDefault() )
                        .toLocalDateTime();
                int numberOfParticipants = resultSet.getInt("NUMBER_OF_PARTICIPANTS");
                String description = resultSet.getString("DESCRIPTION");
                String genre = resultSet.getString("GENRE");
                int sales = resultSet.getInt("SALES");
                int albumCount = resultSet.getInt("ALBUM_COUNT");
                String userCreator= resultSet.getString("USER_CREATOR");

                BD.data.add(new MusicBand(id,name,new Coordinates(x,y),ldt,numberOfParticipants,description,MusicGenre.valueOf(genre),new Label(sales),albumCount,userCreator));
            }

            sql = "SELECT * FROM USERS";
            resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()){
                String name = resultSet.getString("NAME");
                String pass = resultSet.getString("PASS");
                users.add(new User(name,pass));
            }
            BD.sort();
            return true;
        }catch (SQLException e){
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

            PreparedStatement preparedStatement =connection.prepareStatement("INSERT INTO BASE (ID,NAME,X,Y,CREATE_DATE,NUMBER_OF_PARTICIPANTS,DESCRIPTION,GENRE,SALES,ALBUM_COUNT,USER_CREATOR) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1,Integer.parseInt(id));
            preparedStatement.setString(2,name);
            preparedStatement.setInt(3,Integer.parseInt(x));
            preparedStatement.setInt(4,Integer.parseInt(y));
            preparedStatement.setDate(5,creation_date);
            preparedStatement.setInt(6,Integer.parseInt(numberOfParticipants));
            preparedStatement.setString(7,description);
            preparedStatement.setString(8,genre);
            preparedStatement.setInt(9,Integer.parseInt(sales));
            preparedStatement.setInt(10,Integer.parseInt(albumCount));
            preparedStatement.setString(11,user_creator);

            preparedStatement.executeUpdate();
            data.add(musicBand);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    public static boolean addUser(User user)  {
        try {
            Statement statement = connection.createStatement();
            String id = String.valueOf(users.size());
            String name = user.getName();
            String password = sha1(user.getPass());
            String sql = String.format("INSERT INTO USERS (ID, NAME, PASS) VALUES (%s,'%s','%s');",id,name,password);
            System.out.println(sql);
            statement.executeUpdate(sql);
            BD.users.add(new User(name,sha1(user.getPass())));
            return true;
        }catch (SQLException e){
            return false;
        }
    }





    public static String sha1(String password){
        String out = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(password.getBytes(StandardCharsets.UTF_8),0,password.length());
            out = DatatypeConverter.printHexBinary(messageDigest.digest());
        }catch (NoSuchAlgorithmException ignored){

        }
        return out;
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
            String sql = String.format("DELETE from BASE where ID=%d;", id);
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




    private boolean createTable(){
        try {
            Statement statement;
            statement= connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS USERS"+
                    "(ID INT PRIMARY KEY NOT NULL,"+
                    "NAME TEXT NOT NULL,"+
                    "PASS TEXT NOT NULL)";
            statement.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS DATA_BD" +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " NAME                   TEXT    NOT NULL, " +
                    " X                      INT    NOT NULL," +
                    " Y                      INT    NOT NULL," +
                    " CREATE_DATE          DATE    NOT NULL ," +
                    " NUMBER_OF_PARTICIPANTS INT NOT NULL," +
                    " DESCRIPTION            TEXT     NOT NULL," +
                    " GENRE                  TEXT     NOT NULL," +
                    " SALES           INT     NOT NULL," +
                    " ALBUM_COUNT            INT     NOT NULL," +
                    " USER_CREATOR           TEXT     NOT NULL)";
            statement.executeUpdate(sql);
            return true;
        }catch (SQLException e){
            return false;
        }


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



    public static boolean checkPass(String name, String pass){
        System.out.println(name + "   " + pass);
        System.out.println(users.toString());
        User result = null;
        for(User user : users){
            if(user.getName().equals(name)){
                result = user;
                break;
            }
        }
        if(result != null){
            //Неправильный пароль
            return result.getPass().equals(sha1(pass));//успешная авторизация
        }
        return false;
    }



}
