package lab.BasicClasses;

import java.io.Serializable;

/** Музыкальные жанры*/
public enum MusicGenre implements Comparable<MusicGenre>, Serializable {
    ROCK,
    SOUL,
    BLUES,
    PUNK_ROCK
}