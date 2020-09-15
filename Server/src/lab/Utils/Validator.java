package lab.Utils;



import lab.BasicClasses.Label;
import lab.BasicClasses.MusicBand;
import lab.BasicClasses.MusicGenre;

import java.util.Arrays;

public class Validator {
    private static boolean checkGenre(String toContains) {
        return Arrays.stream(MusicGenre.values()).anyMatch((color) -> color.name().equals(toContains));
    }

    public static boolean validateMusicBand(MusicBand musicBand) {
        try {
            return (musicBand.getName() != null && !musicBand.getName().equals("")) &&
                    musicBand.getCoordinates().getX() > 0  &&
                    musicBand.getNumberOfParticipants() > 0  &&
                    musicBand.getGenre() != null &&
                    musicBand.getAlbumCount() >0 &&
                    musicBand.getDescription()!=null&&
                    validateLabel(musicBand.getLabel());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateLabel(Label label) {
        return (label.getSales()>0) ;
    }
}
