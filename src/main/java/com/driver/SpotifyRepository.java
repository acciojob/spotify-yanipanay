package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;



    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();

    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);

        List<Playlist> userPlayLists = new ArrayList<>();
        userPlaylistMap.put(user,userPlayLists);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        List<Album> albums = new ArrayList<>();
        artistAlbumMap.put(artist,albums);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album(title);
        Artist artist=null;

        for(Artist art : artists){
            if (art.getName()==artistName)
                artist = art;
                break;
        }
        if (artist==null) artist = createArtist(artistName);
        List<Album> albumList  = artistAlbumMap.get(artist);
        albums.add(album);
        albumList.add(album);

        List<Song> songList = new ArrayList<>();
        albumSongMap.put(album,songList);
        artistAlbumMap.put(artist,albumList);

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title,length);

        Album album = null;

        System.out.println(albums.size());
        for(Album album1:albums){

            if(album1.getTitle().equals(albumName)) {
                album = album1;
                break;
            }
        }

        if(album==null) throw new AlbumException("Album does not exist");

        List<Song> songList = albumSongMap.get(album);
        songList.add(song);

        List<User> likedUsers = new ArrayList<>();
        songLikeMap.put(song,likedUsers);

        albumSongMap.put(album,songList);

        songs.add(song);

        return song;
    }


    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = new Playlist(title);
        User user = null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)) {
                user = user1;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        List<Song> songsInPL = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength()==length) songsInPL.add(song);
        }

        List<User> listeners = new ArrayList<>();
        listeners.add(user);
        playlistSongMap.put(playlist,songsInPL);
        playlistListenerMap.put(playlist,listeners);
        creatorPlaylistMap.put(user,playlist);

        List<Playlist> userPlaylists = userPlaylistMap.get(user);
        userPlaylists.add(playlist);
        userPlaylistMap.put(user,userPlaylists);
        playlists.add(playlist);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = new Playlist(title);
        User user = null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)) {
                user = user1;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        List<Song> songsInPL = new ArrayList<>();

        for(Song song : songs){
            if(songTitles.contains(song.getTitle())) songsInPL.add(song);
        }

        List<User> listeners = new ArrayList<>();
        listeners.add(user);
        playlistSongMap.put(playlist,songsInPL);
        playlistListenerMap.put(playlist,listeners);
        creatorPlaylistMap.put(user,playlist);

        List<Playlist> userPlaylists = userPlaylistMap.get(user);
        userPlaylists.add(playlist);
        userPlaylistMap.put(user,userPlaylists);
        playlists.add(playlist);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)) {
                user = user1;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");

        Playlist playlist = null;
        for(Playlist playlist1:playlists){
            if(playlist1.getTitle().equals(playlistTitle)) {
                playlist = playlist1;
                break;
            }
        }
        if(playlist==null) throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.get(user).equals(playlist)) return playlist;
        else{
            List<User> listeners =playlistListenerMap.get(playlist);
            if(listeners.contains(user)) return playlist;
            else {
                listeners.add(user);
                playlistListenerMap.put(playlist,listeners);

                List<Playlist> userPL = userPlaylistMap.get(user);
                userPL.add(playlist);

                return playlist;
            }
        }
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)) {
                user = user1;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");

        Song song = null;
        for(Song song1:songs){
            if(song1.getTitle().equals(songTitle)){
                song=song1;
                break;
            }
        }

        if(song==null) throw new Exception("Song does not exist");

        List<User> likedUsers = songLikeMap.get(song);

        if(likedUsers.contains(user)) return song;
        likedUsers.add(user);
        songLikeMap.put(song,likedUsers);
        song.setLikes(song.getLikes()+1);

        Album album = null;

        for(Album album1 : albums){
            if(albumSongMap.get(album1).contains(song)){
                album = album1;
                break;
            }
        }

        Artist artist = null;
        for(Artist artist1 : artists){
            if(artistAlbumMap.get(artist1).contains(album)){
                artist=artist1;
                break;
            }
        }

        artist.setLikes(artist.getLikes()+1);
        return song;
    }

    public String mostPopularArtist() {
        int likes = 0;
        String artistName = null;

        for(Artist artist : artists){
            if(artist.getLikes()>likes){
                likes = artist.getLikes();
                artistName= artist.getName();
            }
        }
        return artistName;
    }

    public String mostPopularSong() {

        int likes = 0;
        String songName = null;

        for(Song song : songs){
            if(song.getLikes()>likes){
                likes = song.getLikes();
                songName= song.getTitle();
            }
        }
        return songName;
    }
}
