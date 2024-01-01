import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class view  {
    private JFrame frame;//main frame
    private JPanel sideBar;//left side bar

    private JPanel userDashboardPanel;//for dashboard in sidebar

    private JPanel playerPanel;//bottom player panel;

    private JLabel profilePicture;//label to hold profile pic in the sidebar

    private JButton userDashboard;//user dashboard in the sidebar

    private JPanel leaderBoardPanel;//panel in userdashboard that shows top 5 songs
    private JButton songs;//all songs in the sideBar

    private JButton playlists;//all playlists in the sideBar

    private JButton account;//account tab in the sideBar

    private JButton settings;//settings tab in  the sidebar

    private JPanel settingsPanel;//panel for settings tab

    private JPanel accountPanel;//panel for account tab

    private JPanel playlistPanel;//panel for playlist tab

    private JLabel globalUsername;//global username for the sidepanel

    private JLabel  welcomeLabel;//for the userdashboard e.g. Welcome, username

    private JButton pausePlayButton;//for the botom player panel

    private JButton nextButton;//for the bottom player panel

    private JButton previousButtton;//for the bottom player panel

    private JPanel musicPanel;//the actual panel for the users songs

    private JScrollPane tableContainer;//the container that holds the table

    private DefaultTableModel model;  // for the music table

    private JButton changeUsernameButton;

    private JButton changeProfilePictureButton;//to change users profile picture in account tab


   private JTable table;//for the all music table

    private JLabel fontLabel = new JLabel("Font");

   private Font currentFont = fontLabel.getFont();
   private Font headingFont = new Font(currentFont.getName(), currentFont.getStyle(), 20);

   private Font normalFont = new Font(currentFont.getName(), currentFont.getStyle(), 15);

   private Font tableFont = new Font("Courier", Font.BOLD, 15);
    private Font smallFont = new Font(currentFont.getName(), currentFont.getStyle(), 10);
    private JButton folderButton;//in settings to sync folder

   private JButton quitButton;//in settings to quit application

   private JLabel currentlyPlaying;//for the player panel and shows the current song playing

   private JLabel currentArtist;//for the player panel that shows the current artist below the song

    private JLabel albumLabel;//label to hold profile pic in the sidebar


    private final String[] tableColumns = {"#","Title", "Artist", "Album", "Duration"};//headers for the table

    private JSlider audioSlider;//adjusts the volume for the track playing, its in the player panel at the bottom

    private JProgressBar songLengthBar;//for the song length bar in the player panel, shows the song progression

    private JLabel songLength;//for the song length next to the song bar
    public view() {//this constructor mostly initialises everything and calls the panels
        frame = new JFrame();
        frame.setTitle("Music Player");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(34, 34, 34, 34));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        sideBar = new JPanel();
        playerPanel = new JPanel();
        profilePicture = new JLabel();
        albumLabel = new JLabel();
        globalUsername = new JLabel();
        userDashboard = new JButton();
        userDashboardPanel = new JPanel();
        leaderBoardPanel = new JPanel();
        settingsPanel = new JPanel();
        playlistPanel = new JPanel();
        songs = new JButton();
        playlists = new JButton();
        account = new JButton();
        settings = new JButton();
        pausePlayButton = new JButton();
        nextButton = new JButton();
        previousButtton = new JButton();
        accountPanel = new JPanel();
        musicPanel = new JPanel();
        tableContainer = new JScrollPane();
        folderButton = new JButton();
        quitButton = new JButton();
        changeUsernameButton = new JButton("Change Username");
        changeProfilePictureButton = new JButton("Change Profile Picture");
        table = new JTable();
        currentlyPlaying = new JLabel(" ");
        currentlyPlaying.setFont(tableFont);
        currentArtist = new JLabel(" ");
        currentArtist.setFont(tableFont);
        audioSlider = new JSlider(0,100,25);//minimum is 0, max is 100, set default to 25
        songLengthBar = new JProgressBar(0,100);
        songLength = new JLabel("");

        //calling all panels, all are hidden except from userdashboard because thats our landing page
        setSideBar();
        showPlayerPanel();
        showUserDashboard();

        showMusicDashboard();
        showAccountDashboard();
        showSettingsDashboard();
        showPlaylistDashboard();


    }

    public void setSideBar() {//sidebar, sets the buttons and adds the user profile picture to the top left
        sideBar.setLayout(null);
        sideBar.setBounds(0, 0, 200, 495);
        sideBar.setBackground(new Color(34, 34, 34));
        frame.add(sideBar);

        JPanel profilePanel = new JPanel();
        sideBar.add(profilePanel);
        profilePanel.setLayout(null);
        profilePanel.setBackground(new Color(47, 45, 45,255));
        profilePanel.setBounds(0,0,200,180);
        profilePanel.setBorder(new LineBorder(Color.darkGray));

        ImageIcon originalProfilePicture = new ImageIcon(getClass().getResource("user.png"));
        ImageIcon resizedProfilePicture = new ImageIcon(originalProfilePicture.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH));
        profilePicture.setIcon(resizedProfilePicture);
        profilePanel.add(profilePicture);
        profilePicture.setBounds(0, 0, 199, 150);
        globalUsername.setText("User");
        globalUsername.setFont(normalFont);
        profilePanel.add(globalUsername);
        globalUsername.setBounds(80, 155, 150, 20);
        globalUsername.setForeground(Color.white);
        globalUsername.setFont(normalFont);




        sideBar.add(userDashboard);//userDashboard Button on the left
        userDashboard.setText("Dashboard \uD83D\uDCCB");
        userDashboard.setFocusable(false);
        userDashboard.setBounds(0, 190, 200, 50);
        userDashboard.setBackground(new Color(47, 45, 45,255));
        userDashboard.setForeground(Color.white);
        userDashboard.setFont(normalFont);


        sideBar.add(songs);
        songs.setText("Songs ♫");
        songs.setFocusable(false);
        songs.setForeground(Color.white);
        songs.setBounds(0, 255, 200, 50);
        songs.setBackground(new Color(47, 45, 45,255));
        songs.setFont(normalFont);




        sideBar.add(playlists);
        playlists.setText("Playlists \uD83C\uDFB6");
        playlists.setFocusable(false);
        playlists.setBounds(0, 320, 200, 50);
        playlists.setBackground(new Color(47, 45, 45,255));
        playlists.setForeground(Color.white);
        playlists.setFont(normalFont);


        sideBar.add(account);
        account.setText("Account \uD83D\uDC64");
        account.setFocusable(false);
        account.setBounds(0, 380, 200, 50);
        account.setBackground(new Color(47, 45, 45,255));
        account.setForeground(Color.white);
        account.setFont(normalFont);


        sideBar.add(settings);
        settings.setText("Settings \uD83D\uDD27");
        settings.setFocusable(false);
        settings.setBounds(0, 440, 200, 50);
        settings.setBackground(new Color(47, 45, 45,255));
        settings.setForeground(Color.white);
        settings.setFont(normalFont);


        frame.setVisible(true);


    }

    public void showUserDashboard(){//user dashboard
        userDashboardPanel.setBackground(new Color(47, 47, 47, 255));
        userDashboardPanel.setBounds(200, 0, 800, 600);
        userDashboardPanel.setLayout(null);
        frame.add(userDashboardPanel);
        userDashboardPanel.setVisible(false);


        //formats the current date and time for the user dashboard
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        System.out.println(globalUsername.getText());
        welcomeLabel = new JLabel("Welcome, " + globalUsername.getText());
        JLabel dateLabel = new JLabel(dateFormat.format(cal.getTime()));
        userDashboardPanel.add(welcomeLabel);
        userDashboardPanel.add(dateLabel);
        welcomeLabel.setBounds(10,10,250,50);
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setFont(headingFont);
        dateLabel.setBounds(540,10,300,50);
        dateLabel.setForeground(Color.white);
        dateLabel.setFont(headingFont);


        //this panel is for the top 5 songs
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(Color.darkGray);
        headerPanel.setBounds(10,80,300,30);
        JLabel titleLabel = new JLabel("Your Top 5 Played Songs");
        titleLabel.setBounds(80,5,200,20);
            titleLabel.setForeground(Color.white);
        headerPanel.add(titleLabel);
        userDashboardPanel.add(headerPanel);

        userDashboardPanel.add(leaderBoardPanel);
        leaderBoardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        leaderBoardPanel.setBounds(10,110,300,250);
        leaderBoardPanel.setBackground(new Color(37, 35, 35,255));

        userDashboardPanel.setVisible(true);//so when the program is loaded, we always come to the dashboard panel


        frame.repaint();
    }

    public void updateLeaderboardPanel(controller c) {  // Get the list of songs from the controller and display it in user dashboard. top 5 songs
       leaderBoardPanel.removeAll();//so the panel updates

        ArrayList<song> myData = c.getSongs();//gets the arraylist from controller


        if (!myData.isEmpty()) {//if its not empty, add it to the panel with labels for the song name and counter
            int counter = 0;
            int size = Math.min(5, myData.size());//if the list is greater than 5, we pick 5 because we only wan to show 5 songs
            while (counter < size) {
                JLabel songLabel = new JLabel();
                JLabel counterLabel = new JLabel();
                songLabel.setText(myData.get(counter).getSongName());
                counterLabel.setText(String.valueOf(myData.get(counter).getNumberOfPlays()));
                songLabel.setForeground(Color.white);
                counterLabel.setForeground(Color.white);
                songLabel.setFont(normalFont);
                counterLabel.setFont(normalFont);
                songLabel.setPreferredSize(new Dimension(220, 40));
                counterLabel.setPreferredSize(new Dimension(50, 40));
                leaderBoardPanel.add(songLabel);
                leaderBoardPanel.add(counterLabel);


                counter++;
            }


        }
    }

    public void showPlayerPanel() {//bottom player panel
        playerPanel.setLayout(null);
        playerPanel.setBackground(new Color(34, 34, 34));
        frame.add(playerPanel);
        playerPanel.setBounds(0, 495, 1000, 80);

        pausePlayButton.setText("▶");
        nextButton.setText(">>>");
        previousButtton.setText("<<<");
        pausePlayButton.setFocusable(false);
        nextButton.setFocusable(false);
        previousButtton.setFocusable(false);



        playerPanel.add(pausePlayButton);
        playerPanel.add(nextButton);
        playerPanel.add(previousButtton);

        pausePlayButton.setBounds(480, 20, 100, 40);
        previousButtton.setBounds(360, 20, 100, 40);
        nextButton.setBounds(600, 20, 100, 40);

        pausePlayButton.setBackground(new Color(47, 45, 45,255));
        previousButtton.setBackground(new Color(47, 45, 45,255));
        nextButton.setBackground(new Color(47, 45, 45,255));
        pausePlayButton.setForeground(Color.white);
        previousButtton.setForeground(Color.white);
        nextButton.setForeground(Color.white);

            //for the album image
        ImageIcon originalAlbumBackground = new ImageIcon(getClass().getResource("audioImage.png"));
        ImageIcon resizedAlbumBackground = new ImageIcon(originalAlbumBackground.getImage().getScaledInstance(70, 65, Image.SCALE_SMOOTH));
        albumLabel.setIcon(resizedAlbumBackground);
        playerPanel.add(albumLabel);
        albumLabel.setBounds(0, 0, 100, 65);

        //for the song currently playing
        playerPanel.add(currentlyPlaying);
        currentlyPlaying.setBounds(75,10,300,20);
        currentlyPlaying.setForeground(Color.white);
        currentlyPlaying.setFont(normalFont);

        //for the artist currently playing
        playerPanel.add(currentArtist);
        currentArtist.setBounds(75,30,300,20);
        currentArtist.setForeground(Color.white);
        currentArtist.setFont(normalFont);

        //speaker symbol next to volume slider
        JLabel audioLabel = new JLabel("\uD83D\uDD0A");
        playerPanel.add(audioLabel);
        audioLabel.setBounds(715,10,30,45);
        audioLabel.setForeground(Color.white);



        //audioslider for songs, adjusts the volume
        playerPanel.add(audioSlider);
        audioSlider.setBounds(730,25,250,40);//setting the bounds
        audioSlider.setMajorTickSpacing(25);//each milestone is 25
        audioSlider.setMinorTickSpacing(5);//each small milestone is 5
        //audioSlider.setPaintTicks(true);//shows the milestone ticks
        audioSlider.setBackground(new Color(34, 34, 34));
        audioSlider.setPaintLabels(true);//shows the number values on the slider
        audioSlider.setForeground(Color.white);




        playerPanel.add(songLengthBar);//adds the length bar to show progression of the song
        songLengthBar.setBounds(360,5,340,10);
        songLengthBar.setBackground(new Color(34,34,34));
        songLengthBar.setForeground(Color.white);


        playerPanel.add(songLength);//the song length next to the bar to indicate to user
        songLength.setBounds(710,5,150,10);
        songLength.setForeground(Color.white);
        songLength.setFont(smallFont);




    }

    public void showAccountDashboard() {
        accountPanel.setBackground(new Color(47, 47, 47, 255));
        accountPanel.setBounds(200, 0, 800, 600);
        accountPanel.setLayout(null);
        frame.add(accountPanel);

        JLabel titleLabel = new JLabel("Account Settings");
        accountPanel.add(titleLabel);
        titleLabel.setBounds(350,0,300,50);
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(headingFont);

        accountPanel.add(changeUsernameButton);
        changeUsernameButton.setBounds(180,100,200,70);
        changeUsernameButton.setBackground(new Color(47, 45, 45,255));
        changeUsernameButton.setForeground(Color.white);
        changeUsernameButton.setFocusable(false);


        accountPanel.add(changeProfilePictureButton);
        changeProfilePictureButton.setBounds(400,100,200,70);
        changeProfilePictureButton.setBackground(new Color(47, 45, 45,255));
        changeProfilePictureButton.setForeground(Color.white);
        changeProfilePictureButton.setFocusable(false);


        accountPanel.setVisible(false);
        frame.repaint();


    }

    public void showMusicDashboard() {//the music table where user can click and play songs
        musicPanel.setBackground(new Color(47, 47, 47, 255));
        musicPanel.setBounds(200, 0, 800, 600);
        musicPanel.setLayout(null);
        frame.add(musicPanel);

        JLabel musicTitle = new JLabel("All Songs");
        musicPanel.add(musicTitle);
        musicTitle.setBounds(355, 0, 150, 20);
        musicTitle.setForeground(Color.white);
        musicTitle.setFont(normalFont);

        // Create a DefaultTableModel with placeholder data and column names
        model = new DefaultTableModel(new Object[][]{}, tableColumns);

        JScrollPane tableContainer = new JScrollPane();
        tableContainer.setBackground(new Color(47, 45, 45, 255));
        tableContainer.setBounds(40, 30, 700, 450);
        musicPanel.add(tableContainer);
        tableContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tableContainer.getVerticalScrollBar().setBackground(new Color(47, 45, 45, 255));


        table = new JTable(model);
        table.setEnabled(false);
        table.setBackground(new Color(47, 45, 45, 255));
        table.setForeground(Color.white);
        table.getTableHeader().setBackground(new Color(47, 45, 45, 255));
        table.getTableHeader().setForeground(Color.white);
        table.getTableHeader().setFont(normalFont);
        table.setFont(tableFont);
        table.setRowHeight(35); // Set the desired row height
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);












        tableContainer.getViewport().setBackground(new Color(47, 45, 45, 255));
        tableContainer.setViewportView(table);

        musicPanel.setVisible(false);

        frame.repaint();
    }

    public void updateMusicTable(Object[][] newData) {//updating the table
        model.setDataVector(newData, tableColumns);
    }


    public void showSettingsDashboard(){
        settingsPanel.setBackground(new Color(47, 47, 47, 255));
        settingsPanel.setBounds(200, 0, 800, 600);
        settingsPanel.setLayout(null);
        frame.add(settingsPanel);
        settingsPanel.setVisible(false);

        JLabel titleLabel = new JLabel("App Settings");
        settingsPanel.add(titleLabel);
        titleLabel.setBounds(350,0,300,50);
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(headingFont);


        settingsPanel.add(folderButton);
        folderButton.setText("Import Songs");
        folderButton.setBounds(180,100,200,70);
        folderButton.setFocusable(false);
        folderButton.setBackground(new Color(47, 45, 45,255));
        folderButton.setForeground(Color.white);

        settingsPanel.add(quitButton);
        quitButton.setText("Exit System");
        quitButton.setBounds(400,100,200,70);
        quitButton.setFocusable(false);
        quitButton.setBackground(new Color(47, 45, 45,255));
        quitButton.setForeground(Color.white);



        frame.repaint();

    }

    public void showPlaylistDashboard(){
        playlistPanel.setBackground(new Color(47, 47, 47, 255));
        playlistPanel.setBounds(200, 0, 800, 600);
        playlistPanel.setLayout(null);
        frame.add(playlistPanel);
        playlistPanel.setVisible(false);
        JLabel musicTitle = new JLabel("Playlists");
        playlistPanel.add(musicTitle);
        musicTitle.setBounds(370, 0, 150, 20);
        musicTitle.setForeground(Color.white);
        musicTitle.setFont(tableFont);




        JScrollPane playlistContainer = new JScrollPane();
       playlistContainer.setBackground(new Color(47, 45, 45,255));
        playlistContainer.setBounds(40, 30, 700, 450);
        playlistPanel.add(playlistContainer);
        playlistContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);//add a scroller for vertical
        playlistContainer.getVerticalScrollBar().setBackground(new Color(47, 45, 45,255));//changes color of scroller



        playlistContainer.getViewport().setBackground(new Color(47,45,45,255));//setting background of playlistContainer


        frame.repaint();
    }



    //getters for buttons and panels below this point


    public JButton getUserDashboard(){
        return this.userDashboard;
    }
    public JButton getSongButton() {
        return this.songs;
    }

    public JButton getPlaylistButton() {
        return this.playlists;
    }

    public JButton getAccountButton() {
        return this.account;
    }

    public JButton getSettingsButton() {
        return this.settings;
    }


    public JPanel getUserDashboardPanel(){
        return this.userDashboardPanel;
    }
    public JPanel getPlayerPanel() {
        return this.playerPanel;
    }

    public JPanel getAccountPanel() {
        return this.accountPanel;
    }


    public JPanel getSettingsPanel(){
        return this.settingsPanel;
    }


    public JPanel getMusicPanel() {
        return this.musicPanel;
    }

    public JPanel getSideBar() {
        return this.sideBar;
    }

    public JPanel getPlaylistPanel(){
        return this.playlistPanel;
    }


    public JButton getUploadButton(){
        return this.folderButton;
    }

    public JButton getQuitButton(){
        return this.quitButton;
    }

    public JTable getMusicTable(){
        return this.table;
    }

    public JFrame getFrame(){
        return frame;
    }

    public JButton getPausePlayButton(){
        return this.pausePlayButton;
    }

    public JButton getNextButton(){
        return this.nextButton;
    }

    public JButton getPreviousButtton(){
        return this.previousButtton;
    }

    public JLabel getCurrentlyPlaying(){
        return this.currentlyPlaying;
    }

    public JLabel getAlbumLabel(){
        return this.albumLabel;
    }

    public JLabel getCurrentArtistLabel(){
        return this.currentArtist;
    }

    public JSlider getAudioSlider(){
        return this.audioSlider;
    }

    public JProgressBar getSongLengthBar(){
        return this.songLengthBar;
    }

    public JLabel getSongLength(){
        return this.songLength;
    }

    public JButton getProfilePictureButton(){
        return this.changeProfilePictureButton;
    }

    public JLabel getProfilePicture(){//so the controller can change the profile picture
        return this.profilePicture;
    }

    public JButton getChangeUsernameButton(){//so the controller can change the username
        return this.changeUsernameButton;
    }

    public JLabel getGlobalUsername(){
        return this.globalUsername;
    }

    public JButton getFolderButton(){
        return this.folderButton;
    }

    public JLabel getWelcomeLabel(){
        return this.welcomeLabel;
    }

}



