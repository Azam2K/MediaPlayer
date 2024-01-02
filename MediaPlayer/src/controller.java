import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import jaco.mp3.player.MP3Player;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;


//https://sourceforge.net/projects/jacomp3player/     //This is for the mp3 player
//https://jacomp3player.sourceforge.net/              //This is for the mp3 player


//https://jthink.net/jaudiotagger/index.jsp    This is for the metadata
//https://bitbucket.org/ijabz/jaudiotagger/src/master/   This is for the metadata
public class controller implements ActionListener, MouseListener, ChangeListener {
    private view view;//creates a view object

    private ArrayList<song> Songs = new ArrayList<song>();//holds our song objects with a counter of each song to display in the user dashboard


    private Clip wavFilePlayer;//clip for the song to play



    static Object[][] tableData;//actual data for the table, aka the row data

   private String fileName;//song name used in the play function

   private String globalSongName;//just the global song name that shows in the player panel of the view

    private  MP3Player mp3player;//plays mp3 audio uses library

    private Boolean playing;//determines if a song is playing

   private File[] musicFiles;//holds all the actual audio files from the user

    private File currentSongFile;
    private boolean buttonClickable = true;//remove this, its useless

    int currentSliderLevel;//current volume level of the song

   private AudioFile f;//library to read metadata

    private Tag tag;//library to read metadata

    private String artistName;//for the artistname in the view when a song is playing in the bottom left

   private  int row;//the row of the table playing, I use this for the next and previous buttons

   private Boolean audioLevelSet = false;//determines if the volume level has been updated by the user
   private AudioHeader AudioHeader;//library to read metadata
    private Timer timer;//the timer for the song playing

    private TimerTask songTimer;//for the execution of te timer
    private int songLength;//the song length of the current song length playing

    private boolean paused;//to check if the song is paused or not

    private int lengthCounter;//this is used to increment the progress bar for the song




    public controller(view View) {
        this.view = View;
        setupListeners();//setup all the button, table and other listeners from the view

        // Delay the update of the music table until the data is loaded


    }


    public void getMusicFolder(File folder) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        if (folder == null || !folder.isDirectory()) {//handles invalid folders
            System.out.println("Invalid folder specified.");
            return;
        }
        musicFiles = folder.listFiles();
        int elementCounter = 0;//count the number of valid files in the folder
        for (File file : musicFiles) {
            if (isValidAudioFile(file)){
                elementCounter++;
            }
        }
        tableData = new Object[elementCounter][5];//loop here to count the mp3 and wav files and assign it to array to avoid bloat


        int counter = 0;//for each row in the table, just a counter for how many songs we have
        for (File file : musicFiles) {
            if (isValidAudioFile(file)) {
                    int dotIndex = file.getName().lastIndexOf('.');//gets the final dot in the string, everything after the last . isn't took, so this gets the actual file name
                fileName = file.getName().substring(0, dotIndex);//creates substring, starts at start and ends where dot is


                f = AudioFileIO.read(file);//setup for the file
                tag = f.getTag();//setup for file metadata
                AudioHeader = f.getAudioHeader();//setup for file metadata


                int trackLength = f.getAudioHeader().getTrackLength();//uses library to get track length
                int minutes = (trackLength % 3600) / 60;//get minutes
                int seconds = trackLength % 60;//gets seconds


                String artistName;
                String albumName;
                String songLength;
                String songName;


                //formats into minutes and seconds
                if (tag != null){//if theres no error reading anything from the file then read it
                    artistName = tag.getFirst(FieldKey.ALBUM_ARTIST);//uses library to get artist name metadata from the file
                    albumName = tag.getFirst(FieldKey.ALBUM);//uses library to get album name
                     songName = tag.getFirst(FieldKey.TITLE);//uses library to get album name


                }else{//else the albumname and artistname are gonna be empty
                    albumName = " ";
                    artistName = " ";
                    songName = fileName;
                }
                songLength = String.format("%2d:%02d", minutes, seconds);//formats into minutes and seconds

                if (!songName.isEmpty()){
                    fileName = songName;
                }

                //data for the table
                    tableData[counter][0] = counter + 1;
                    tableData[counter][1] = fileName;
                    tableData[counter][2] = artistName;
                    tableData[counter][3] = albumName;
                    tableData[counter][4] = songLength;
                    counter++;
                }
            }
        }

    public Boolean isValidAudioFile(File audioFile){//checks if the file is an mp3 or wav
        if (audioFile.isFile() && audioFile.getName().toLowerCase().length() > 3) {
            String fileName = audioFile.getName();
            if ((fileName.charAt(fileName.length() - 1) == '3' &&
                    fileName.charAt(fileName.length() - 2) == 'p' &&
                    fileName.charAt(fileName.length() - 3) == 'm') ||
                    (fileName.charAt(fileName.length() - 1) == 'v' &&
                            fileName.charAt(fileName.length() - 2) == 'a' &&
                            fileName.charAt(fileName.length() - 3) == 'w')) {
                return true;
            }
        }
        return false;
    }

    public Boolean isMP3(File audioFile){//checks if file is an mp3 file by checking last 3 chars. this is used to add to the list of songs
        if (audioFile.isFile() && audioFile.getName().length() > 3) {
            String fileName = audioFile.getName();
            if ((fileName.charAt(fileName.length() - 1) == '3' &&
                    fileName.charAt(fileName.length() - 2) == 'p' &&
                    fileName.charAt(fileName.length() - 3) == 'm')) {
                return true;
            }
        }
        return false;
    }

    public Boolean isWAV(File audioFile){//checks if the file is a wav file by checking its last 3 chars. this is used to add to the list of songs
        if (audioFile.isFile() && audioFile.getName().length() > 3) {
            String fileName = audioFile.getName();
            if ((fileName.charAt(fileName.length() - 1) == 'v' &&
                    fileName.charAt(fileName.length() - 2) == 'a' &&
                    fileName.charAt(fileName.length() - 3) == 'w')) {
                return true;
            }
        }
        return false;
    }

    public void removeExtension(String fileName){

    }

public void playSong(File musicFile) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
    playing = false;
    if (isValidAudioFile(musicFile)) {
        currentSongFile = musicFile;//we have a valid file
        f = AudioFileIO.read(musicFile);//setup for the file
        AudioHeader = f.getAudioHeader();//setup for file metadata
        songLength = f.getAudioHeader().getTrackLength();//get the tracklength of the song so the progress bar can be updated
    }

    if(isMP3(currentSongFile) && mp3player != null && !isWAV(currentSongFile)){//if our file is an mp3, and our mp3 player isn't null and we don't have a wav
        if (wavFilePlayer != null){//if our wav player is alive, kill it
            wavFilePlayer.stop();
            wavFilePlayer = null;
        }
        mp3player.stop();//stop mp3 player
        mp3player = null;//set it as null
        mp3player = new MP3Player(currentSongFile);//create new mp3 player with the current file to play
        view.getPausePlayButton().setText("||");//this has to be here because the song is playing
        mp3player.play();//play the song
        playing = true;//its playing
        mp3player.setRepeat(false);//so the song doesnt loop when done

        if (!audioLevelSet){//if volume level isn't already set, set it as default which is 25
            mp3player.setVolume(25);//set it to 25 as default
        }
        else{
            mp3player.setVolume(currentSliderLevel);//playing nextt song and keep volume consistent
        }
    }
    else if (isMP3(currentSongFile) && mp3player == null){//if we're trying to play an mp3 for the first time, play it
        mp3player = new MP3Player(currentSongFile);
        view.getPausePlayButton().setText("||");//this has to be here because the song is playing
        mp3player.play();//play the song
        playing = true;//its playing
        mp3player.setRepeat(false);//so the song doesnt loop when done
        if (!audioLevelSet){//if volume level isn't already set, set it as default which is 25
            mp3player.setVolume(25);//set it to 25 as default
        }
        else{
            mp3player.setVolume(currentSliderLevel);//playing nextt song and keep volume consistent
        }
        if (wavFilePlayer != null){//if the wav file player isn't null, stop it and set it null
            wavFilePlayer.stop();
            wavFilePlayer = null;//set the wavplayer as null
        }
    }
    try {
        //if we dont have an mp3, we have a wav
        if ((isWAV(currentSongFile)) && !playing){//if its a wav
            if (mp3player != null){//if a song is already playing and its mp3, stop it and make it null
                mp3player.stop();
                mp3player = null;
            }
            if (wavFilePlayer != null){//if the wav player is already playing, stop it and set it to null
                wavFilePlayer.stop();
                wavFilePlayer = null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(currentSongFile);
            wavFilePlayer = AudioSystem.getClip();
            wavFilePlayer.open(audioStream);
            wavFilePlayer.start();//starts the song, has to go after the thing above so audio level stays the same
            playing = true;//we're playing
            currentSliderLevel = view.getAudioSlider().getValue();
            int wavAudioLevel = 50 + (int) Math.round((currentSliderLevel - 1) * (100 - 50) / 99.0);//calculation for the sweetspot
            FloatControl gainControl = (FloatControl) wavFilePlayer.getControl(FloatControl.Type.MASTER_GAIN);//gettin the players audio
            float minGain = gainControl.getMinimum();//getting min gain
            float maxGain = gainControl.getMaximum();//gettin max gain
            float mappedGain = minGain + (wavAudioLevel / 100.0f) * (maxGain - minGain);//calculations to make wav player audio level sound close to mp3
            gainControl.setValue(mappedGain);
            long microsecondLength = wavFilePlayer.getMicrosecondLength();//wav player gets in microseconds
            double secondLength = (double) microsecondLength / 1_000_000.0;//conver the microseconds to actual seconds
            songLength = (int) secondLength;//casts it to an int. for the updatesonglength function to use
        }

        if (playing){//if any song is playing, mp3 or wav
            view.getPausePlayButton().setText("||");//this has to be here because the song is playing
            view.getCurrentlyPlaying().setText(globalSongName);//shows the song playing in the bottom left
            view.getCurrentArtistLabel().setText(artistName);//shows the artist name in the bottom left
            view.getPausePlayButton().setText("||");//this has to be here because the song is playing so the next operation would be pause
            updateSongLength(songLength);//update the audio lengh bar in the playerpanel for each respective song
            getArtWork(currentSongFile);//gets the artwork for the song and shows it to the user
        }
    } catch (Exception ignore) {//if there is any sort of problem with the song, it'll change the album image and say bad file
        System.out.println("Can't successfully retreive all info from file.");
        ImageIcon originalAlbumBackground = new ImageIcon(getClass().getResource("/assets/audioImage.png"));
        ImageIcon resizedAlbumBackground = new ImageIcon(originalAlbumBackground.getImage().getScaledInstance(70, 65, Image.SCALE_SMOOTH));
        view.getAlbumLabel().setIcon(resizedAlbumBackground);
    }

    }



    //this gets the artwork and updates the label in the gui showing the current artwork
        public void getArtWork(File currentFile) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
             AudioFile tagFile = AudioFileIO.read(currentFile);//library to read metadata
                Tag ArtWorkTag = f.getTag();//library to get the tag
            if (ArtWorkTag.getFirstArtwork().getBinaryData() != null) {
                byte[] imageArray = ArtWorkTag.getFirstArtwork().getBinaryData();//gets the binary data from the file
                ImageIcon coverArtIcon = new ImageIcon(imageArray);//creates an icon out of the byte array
                Image originalImage = coverArtIcon.getImage();//gets the original icon
                Image resizedImage = originalImage.getScaledInstance(70, 65, Image.SCALE_SMOOTH);//resizes it so it fits
                ImageIcon resizedIcon = new ImageIcon(resizedImage); // Set the resized ImageIcon as the icon for the JLabel
                view.getAlbumLabel().setIcon(resizedIcon);//updates the label in the view
            }
        }


    public Object[][] getMusicData(){//returns the table row info
        return tableData;
    }

    public ArrayList<song> getSongs(){//returns the arraylist of song objects for the view to use
        return this.Songs;
    }

    public void pauseSong() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {//if wav is playing and not null, play it, if mp3 is not null and isn't paused, play it
        if (wavFilePlayer != null && wavFilePlayer.isRunning()) {//if we have a wav file playing
            paused = true;
            wavFilePlayer.stop();
            view.getPausePlayButton().setText("▶");
        } else if (mp3player != null && !mp3player.isPaused()) {//if we have an mp3 file playing
            paused = true;
            mp3player.pause();
            view.getPausePlayButton().setText("▶");
        } else {
            unpauseSong();
        }
    }



    public void unpauseSong() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {//if wav isnt null and isnt running, play it, if mp3 isnt null and is paused, play it
        if (wavFilePlayer != null && !wavFilePlayer.isRunning()) {//if we have a wav file
            wavFilePlayer.start();
            paused = false;
            view.getPausePlayButton().setText("||");
        } else if (mp3player != null && mp3player.isPaused()) {//if we have an mp3 file
            paused = false;
            mp3player.play();
            view.getPausePlayButton().setText("||");
        }
        if (currentSongFile != null){
            if (mp3player != null && mp3player.isStopped()){
                playSong(currentSongFile);
            }
            else if (wavFilePlayer != null && !wavFilePlayer.isRunning()){
                playSong(currentSongFile);
            }
        }
    }

    public void playNextSong() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {//play the next song
         row = row + 1;//getting the next row of the table
        if (playing && row >= 0 && row <= view.getMusicTable().getRowCount() - 1){//if a song is playing and the row isn't negative or the row we're on isn't greater than the amount of total rows
            String songName = (String) view.getMusicTable().getValueAt(row, 1);//gets the actual song name from the row clicked
            artistName = (String) view.getMusicTable().getValueAt(row, 2);//gets the actual song name from the row clicked
            String artistAlbum = (String) view.getMusicTable().getValueAt(row, 3);//gets the actual song name from the row clicked

            globalSongName = (String) view.getMusicTable().getValueAt(row, 1);//gets the actual song name from the row clicked

            try {
                File musicFile = findSong(songName,artistName,artistAlbum);//finds the file from the findSong Function
                playSong(musicFile);//actually plays the song if its found
            } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException |
                     IOException ex) {
                throw new RuntimeException(ex);
            }
            view.getMusicTable().clearSelection();//clears the previously highlighted song
            view.getMusicTable().setRowSelectionInterval(row, row);//highlights the new row
            addSongToList();//adds the current song played to the arraylist

            artistName = (String) view.getMusicTable().getValueAt(row, 2);//gets the actual song name from the row clicked
            view.getCurrentArtistLabel().setText(artistName);//updates label in the playerpanel
            String songLength = (String) view.getMusicTable().getValueAt(row, 4);//gets the song length from the table
            view.getSongLength().setText(songLength);//shows the song length in the playerpanel in the view so itt shows the lengh of the new song next to the bar
        }
        else{//we've overflown the table length so reduce the counter so that it stays to the previous counter where the song is playing
            row = row - 1;
        }
    }

    public void playPreviousSong() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {//play the previous song
        row = row - 1;//getting the previous row
        if (playing && row != -1){//if a song is playing and if we're not negative in our rows, rows start at index 0
            String songName = (String) view.getMusicTable().getValueAt(row, 1);//gets the actual song name from the row clicked
             artistName = (String) view.getMusicTable().getValueAt(row, 2);//gets the actual song name from the row clicked
            String artistAlbum = (String) view.getMusicTable().getValueAt(row, 3);//gets the actual song name from the row clicked

            globalSongName = (String) view.getMusicTable().getValueAt(row, 1);//gets the actual song name from the row clicked

            try {
                File musicFile = findSong(songName,artistName,artistAlbum);//finds the file from the table
                playSong(musicFile);//calls playSong to play the actual file
            } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException |
                     IOException ex) {
                throw new RuntimeException(ex);
            }
            view.getMusicTable().clearSelection();//clears the previously highlighted song
            view.getMusicTable().setRowSelectionInterval(row, row);//highlights the row
            addSongToList();//adds the current song played to the list

            artistName = (String) view.getMusicTable().getValueAt(row, 2);//gets the actual song name from the row clicked
            view.getCurrentArtistLabel().setText(artistName);//updates label in the playerpanel
            String songLength = (String) view.getMusicTable().getValueAt(row, 4);//gets the song length from the table
            view.getSongLength().setText(songLength);//shows the song length in the playerpanel in the view so itt shows the lengh of the song next to the bar
        }
        else{//row is negative, so we add 1 to make it positive
            row = row + 1;
        }
    }

    public void addSongToList(){//if song exists, incrmement its plays, if it doesn't, create object, add it to the list with 1 play
        boolean found = false;
        for (song i : Songs){//if song is found, we're playing it again so increment the listener count
            if (globalSongName.equals(i.getSongName())){
                i.incrementPlays();
                found = true;
            }
        }
        if (!found){//if the song isn't found, add it to the list and dont incremement the counter. its got 1 play
            Songs.add(new song(globalSongName,1));
        }
    }

    public void updateSongLength(int songDurationInSeconds){//updates the length of the bar of the song playing, shows how long is left and how much is progressed
        view.getSongLengthBar().setMaximum(songDurationInSeconds);//so the bar is dynamic respective to each song
        if (timer != null) {// Stop and cancel the existing timer if it's running
            timer.cancel();
            timer.purge();
            lengthCounter = 0;
        }

        timer = new Timer();//create new timer object
        songTimer = new TimerTask() {//the timer needs a task


            @Override
            public void run() {//this code runs when the timer is running
                if (mp3player != null && mp3player.isPlaying()) {//for the mp3 player
                    lengthCounter++;//incremement the counter
                    view.getSongLengthBar().setValue(lengthCounter);//increment the bar by 1
                }
                else if (wavFilePlayer != null && wavFilePlayer.isRunning()){//for the wav player
                    lengthCounter++;//incremement the counter
                    view.getSongLengthBar().setValue(lengthCounter);//increment the bar by 1
                }
            }
        };
        timer.scheduleAtFixedRate(songTimer, 0, 1000);//run the task, start at 0 and run it every second, 1000 milleseconds = 1 second
    }

   public void getLeaderboard() {//leaderboard shows users top songs for their dashboard
        if (!Songs.isEmpty()) {//if songs arraylist isn't empty, sort them which is in the song class
            Collections.sort(Songs);//this is sorting the users plays of each songs to display in the dashboard, its using the comparable in song
        }
    }



    private void setupListeners() {//action listeners for every button in the gui
        view.getUserDashboard().addActionListener(this);
        view.getSongButton().addActionListener(this);//sidebar button
        view.getPlaylistButton().addActionListener(this);//sidebar button
        view.getAccountButton().addActionListener(this);//sidebar button
        view.getSettingsButton().addActionListener(this);//sidebar button
        view.getUploadButton().addActionListener(this);//for JButton to sync folder in settings
        view.getQuitButton().addActionListener(this);//to quit the system in settings
        view.getPausePlayButton().addActionListener(this);//to get the pause button in the botom panel, this pause/plays the song
        view.getMusicTable().addMouseListener(this);//so the table is clickable
        view.getAudioSlider().addChangeListener(this);//so we get info from the volume slider
        view.getPreviousButtton().addActionListener(this);//play the previous song
        view.getNextButton().addActionListener(this);//play next song
        view.getProfilePictureButton().addActionListener(this);//adds action listener to change profile picture button in the view
        view.getChangeUsernameButton().addActionListener(this);//adds action listener to change username button in the view
    }

    private void syncFiles() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {//user can upload folder of their songs
        JFileChooser fileChooser = new JFileChooser();//jfilechooser
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//only allow user to upload folders

        int result = fileChooser.showOpenDialog(view.getFrame());//make sure its included in the frame

        if (result == JFileChooser.APPROVE_OPTION) {//if user approves this, then good, we have the folder
            File selectedFolder = fileChooser.getSelectedFile();//get folder user chose
            getMusicFolder(selectedFolder);//send it to function to upload to the table in the view
        }
    }

    private void updateProfilePicture() {//for the user to update their profile picture
        JFileChooser fileChooser = new JFileChooser();//make a file chooser
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));//only have image files with certain extensions
        int option = fileChooser.showOpenDialog(view.getFrame());

        if (option == JFileChooser.APPROVE_OPTION) {//if they click ok
            File file = fileChooser.getSelectedFile();//get the file
            ImageIcon originalProfilePicture = new ImageIcon(String.valueOf(file));//get the file
            ImageIcon resizedProfilePicture = new ImageIcon(originalProfilePicture.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH));//resize it
            view.getProfilePicture().setIcon(resizedProfilePicture);//update the profile picture in the view
        }
    }

    private void updateUsername(){//updates the username in the panel and the user dashboard
        String newUsername = JOptionPane.showInputDialog("Enter new username: ");
        if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals(" ")){
            view.getGlobalUsername().setText(newUsername);
            view.getWelcomeLabel().setText("Welcome, " + newUsername);
        }
    }





    @Override
    public void actionPerformed(ActionEvent e) {//for all the buttons in the gui
        if (buttonClickable) {
            if (e.getSource() == view.getSongButton()) {//hides every other panel except from the music panel which shows the clickable table of songs, basically the main panel that the user will use
                System.out.println("Song button clicked");
               // getMusicFolder();//gets the folder dynamically
                view.getMusicPanel().setVisible(true);
                view.updateMusicTable(getMusicData());
                view.getFrame().repaint();
                view.getAccountPanel().setVisible(false);
                view.getUserDashboardPanel().setVisible(false);
                view.getSettingsPanel().setVisible(false);
                view.getPlaylistPanel().setVisible(false);
                if (playing != null){//if a song is playing, this ensures the row is highlighted even if we go to different panels
                    if (playing){
                        view.getMusicTable().setRowSelectionInterval(row, row);//highlights the row everytime the music table is clicked if a song is playing
                    }
                }
            }
            else if (e.getSource() == view.getAccountButton()) {//hides every panel except account panel when account button is clicked
                System.out.println("Account button clicked");
                view.getAccountPanel().setVisible(true);
                view.getMusicPanel().setVisible(false);
                view.getUserDashboardPanel().setVisible(false);
                view.getSettingsPanel().setVisible(false);
                view.getPlaylistPanel().setVisible(false);
                view.getFrame().repaint();
            }
            else if (e.getSource() == view.getSettingsButton()) {//hide every other panel except from the settings panel
                System.out.println("Settings button clicked");
                view.getSettingsPanel().setVisible(true);
                view.getMusicPanel().setVisible(false);
                view.getAccountPanel().setVisible(false);
                view.getUserDashboardPanel().setVisible(false);
                view.getPlaylistPanel().setVisible(false);
                view.getFrame().repaint();
                System.out.println("settings page");
            }
            else if (e.getSource() == view.getUserDashboard()){//hide every other panel except user dashboard and call function to populate top 5 songs in the view
                view.getUserDashboardPanel().setVisible(true);
                view.getMusicPanel().setVisible(false);
                view.getAccountPanel().setVisible(false);
                view.getSettingsPanel().setVisible(false);
                view.getPlaylistPanel().setVisible(false);
                view.getFrame().repaint();
                getLeaderboard();//updates the comparator to dynamically update the favorite songs
                view.updateLeaderboardPanel(this);//lets the view call the controller
            }
            else if (e.getSource() == view.getPlaylistButton()){//hide every other panel except from the playlist when playlist button is clicked
                view.getPlaylistPanel().setVisible(true);
                view.getUserDashboardPanel().setVisible(false);
                view.getMusicPanel().setVisible(false);
                view.getAccountPanel().setVisible(false);
                view.getSettingsPanel().setVisible(false);
                view.getFrame().repaint();
            }
            else if (e.getSource() == view.getUploadButton()){//upload button in settings
                try {
                    syncFiles();//upload the folder of songs
                } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException |
                         IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if (e.getSource() == view.getQuitButton()){//quit button in settings
                System.out.println("Exiting System...");
                    System.exit(0);
            }
            else if (e.getSource() == view.getPausePlayButton()){//play button cannot be clicked if nothings playing
                if (!view.getCurrentlyPlaying().getText().equals(" ")){
                    try {
                        pauseSong();
                    } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException |
                             IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            else if (e.getSource() == view.getNextButton()){
                if (!view.getCurrentlyPlaying().getText().equals(" ")){
                    try {
                        playNextSong();
                    } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException |
                             IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            else if (e.getSource() == view.getPreviousButtton()){
                if (!view.getCurrentlyPlaying().getText().equals(" ")){
                    try {
                        playPreviousSong();
                    } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException |
                             IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            else if (e.getSource() == view.getProfilePictureButton()){
                updateProfilePicture();
            }
            else if (e.getSource() == view.getChangeUsernameButton()){
                updateUsername();
            }
        }
    }

    //This finds the song and passes it to play song to actually play the song from the table
    public File findSong(String songName, String artistName, String artistAlbum) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        for (int i = 0; i < musicFiles.length; i++) {
            if (isValidAudioFile(musicFiles[i])) {
                AudioFile tagFile = AudioFileIO.read(musicFiles[i]);//library to read the file
                Tag fileTag = tagFile.getTag();//library is reading the tags of the file

                //initializing the metadata
                String title;
                String artist;
                String album;

                if (fileTag != null) {//if its not null reading a file, grab the data
                    title = fileTag.getFirst(FieldKey.TITLE);
                    artist = fileTag.getFirst(FieldKey.ALBUM_ARTIST);
                    album = fileTag.getFirst(FieldKey.ALBUM);
                } else {
                    title = "";
                    artist = "";
                    album = "";
                }

                int dotIndex = musicFiles[i].getName().lastIndexOf('.');//if we have no metadata, grab the original file name and stop at extension
                String nameWithoutExtension = musicFiles[i].getName().substring(0, dotIndex);//gets the filename without the extension

                if (!title.isEmpty() && songName.equals(title) && artistName.equals(artist) && artistAlbum.equals(album)) {//if we have metadata, return the file for playSong
                    return musicFiles[i];
                } else if (songName.equals(nameWithoutExtension) && title.isEmpty()) {//if we dont have valid metadata, just get the files original name and return it for playSong to use
                    return musicFiles[i];
                }
            }
        }
        return null;
    }


    @Override
    public void mouseClicked(MouseEvent e) {//for the table row clicks
        row = view.getMusicTable().rowAtPoint(e.getPoint());//gets the row
        String songName = (String) view.getMusicTable().getValueAt(row, 1);//gets the actual song name from the row clicked
        artistName = (String) view.getMusicTable().getValueAt(row, 2);//gets the actual song name from the row clicked
        String artistAlbum = (String) view.getMusicTable().getValueAt(row, 3);//gets the actual song name from the row clicked

        globalSongName = (String) view.getMusicTable().getValueAt(row, 1);//gets the actual song name from the row clicked

        try {
           File musicFile = findSong(songName,artistName,artistAlbum);//finds the song from the table
           playSong(musicFile);//plays the song when its found
        } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException |
                 IOException ex) {
            throw new RuntimeException(ex);
        }

        view.getMusicTable().setRowSelectionInterval(row, row);//highlights the row
        artistName = (String) view.getMusicTable().getValueAt(row, 2);//gets the actual song name from the row clicked
        view.getCurrentArtistLabel().setText(artistName);//updates label in the playerpanel
        addSongToList();//adds the current song played to the list
        String songLength = (String) view.getMusicTable().getValueAt(row, 4);//gets the song length from the table
        view.getSongLength().setText(songLength);//shows the song length in the playerpanel in the view so itt shows the lengh of the song next to the bar
    }

    @Override
    public void stateChanged(ChangeEvent e) {//for the audio slider in the gui to adjust volume
        System.out.println(view.getAudioSlider().getValue());
        if(mp3player != null && wavFilePlayer == null) {//if we have an mp3 file, set the sliders
            if (!(mp3player.getVolume() > 100) && !(mp3player.getVolume() < 0)) {
                currentSliderLevel = view.getAudioSlider().getValue();//get the level of the slider
                mp3player.setVolume(currentSliderLevel);//set the volume of the song to the slider
                audioLevelSet = true;
            }
        }
            else if (wavFilePlayer != null && mp3player == null){
                currentSliderLevel = view.getAudioSlider().getValue();
                int wavAudioLevel = 50 + (int) Math.round((currentSliderLevel - 1) * (100 - 50) / 99.0);//calculation for the sweetspot
                FloatControl gainControl = (FloatControl) wavFilePlayer.getControl(FloatControl.Type.MASTER_GAIN);//gettin the players audio

                float minGain = gainControl.getMinimum();//getting min gain
                float maxGain = gainControl.getMaximum();//gettin max gain

                float mappedGain = minGain + (wavAudioLevel / 100.0f) * (maxGain - minGain);//calculations to make wav player audio level sound close to mp3

            if (currentSliderLevel == 0){//if our slider is at 0, mute it
                gainControl.setValue(minGain);
            }
            else{//else 1-100 values, the reason for this is because FloatControl value is in decibels, meaning it's a logarithmic scale, not a linear one
                gainControl.setValue(mappedGain);//setting the audio for the wav
            }
             audioLevelSet = true;//audio level is set
            }

    }

//these all are useless
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


}