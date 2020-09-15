package lab.BasicClasses;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Объект, который будет сохраняться в базе данных.
 * @autor Цветков Фёдор Николаевич P3132
 * @version 1.1
 */


public class MusicBand implements Comparable<MusicBand>, Serializable {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int numberOfParticipants; //Значение поля должно быть больше 0
    private String description; //Поле не может быть null
    private MusicGenre genre; //Поле не может быть null
    private Label label; //Поле может быть null
    private String user_creator;
    private int albumCount; //Поле не может быть null, Значение поля должно быть больше 0

    /**Конструктор создания объекта
     *
     * @param id Long - ID
     * @param name String - Имя
     * @param coordinates Coordinates - Координаты
     * @param createdate LocalDateTime - Дата создания
     * @param numberOfParticipants int - Количество людей в группе
     * @param description String - Описание
     * @param albumCount int - album
     * @param genre MusicGenre - жанр( ROCK,SOUL,BLUES,PUNK_ROCK)

     */
    public MusicBand(Long id, String name, Coordinates coordinates, LocalDateTime createdate, int numberOfParticipants, String description, MusicGenre genre, Label label, int albumCount, String user_creator){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = createdate;
        this.numberOfParticipants = numberOfParticipants;
        this.description = description;
        this.albumCount=albumCount;
        this.genre = genre;
        this.label = label;
        this.user_creator=user_creator;
    }

    public MusicBand(Label label) {
        this.label = label;
    }

    public void setID(Long id) {
        this.id = id;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }



    public MusicGenre getGenre() {
        return genre;
    }

    public Label getLabel() {
        return label;
    }

    /** Метод, возвращает количество людей в мезыкальной группе.
     *
     * @return int - количество людей.
     */
    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }


    /** Метод, возвращает количество альбомов в мезыкальной группе.
     *
     * @return int - количество альбомов.
     */
    public int getAlbumCount() {
        return albumCount;
    }


    public String getUser_creator() {
        return user_creator;
    }

    /** Метод, возвращает ID.
     *
     * @return long - id.
     */
    public long getID(){
        return this.id;
    }

    /** Метод, возвращает количество продаж из Album.
     *
     * @return long - продажи.
     */
    public long getSales(){
        return this.label.getSales();
    }

    /** Метод, возвращает имя музыкальной группы.
     *
     * @return String - name.
     */
    public String getName() {
        return name;
    }
    /** Метод, возвращает описание.
     *
     * @return String - описание.
     */
    public String getDescription(){
        return this.description;
    }

    /** Метод, парсит аргументы из String в MusicBand
     *
     * @param id id, обычно генерируется с помощью giveID().
     * @param name Имя объекта. Поле не может быть null, Строка не может быть пустой.
     * @param x Координата X. Значение поля должно быть больше -687.
     * @param y Координата Y. Поле не может быть null.
     * @param numberOfParticipants Количество людей в музыкальной группе. Значение поля должно быть больше 0.
     * @param description Описание музыкальной группы. Поле не может быть null.
     * @param establishmentDate Дата создания музыкальной группы. Формат: yyyy-MM-dd. Поле может быть null.
     * @param genre Жанр(PSYCHEDELIC_ROCK, RAP, POP, POST_ROCK, POST_PUNK). Поле не может быть null.
     * @param sales Продажи лучшего альбома.
     * @return MusicBand - результат
     */

    /** Переопределенный метод toString
     *
     * @return возвращает объект в виде текста
     */
    @Override
    public String toString() {
        return
                "|ID: " + id + "|Имя: " + name  + "|Координаты: " + coordinates  + "|Дата добавления в базу: " + creationDate + "|Число участников: " + numberOfParticipants  + "|Описание: " + description  + "|Жанр: " + genre + "|Лейбл: " + label  + "|Количество альбомов: " + albumCount;
    }
    @Override
    public int compareTo(MusicBand anotherMusicBand)
    {
        if (this.id.equals(anotherMusicBand.id)) {
            return 0;
        } else if (this.id < anotherMusicBand.id) {
            return -1;
        } else {
            return 1;
        }
    }
    public String toCsv() {
        return
                id + "," + name  + "," + coordinates  + "," + creationDate + "," + numberOfParticipants  + "," + description  + "," + genre + "," + label  + "," + albumCount;
    }
}