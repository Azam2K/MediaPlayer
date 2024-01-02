import java.lang.reflect.Array;
import java.util.ArrayList;

public class song implements Comparable<song> {

    private String songName;
    private int numberOfPlays;


    public song(String sName, int plays){
        this.songName = sName;
        this.numberOfPlays = plays;
    }

    public String getSongName(){
        return this.songName;
    }

    public void incrementPlays(){
        this.numberOfPlays++;
    }

    public int getNumberOfPlays(){
        return this.numberOfPlays;
    }


    @Override
    public int compareTo(song o) {//compares the number of plays to create a leaderboard for top songs
        if (this.getNumberOfPlays() > o.getNumberOfPlays()){
            return -1;
        }else if(this.getNumberOfPlays() == o.getNumberOfPlays()){
            return 0;
        }
        else{
            return 1;
        }
    }
}
