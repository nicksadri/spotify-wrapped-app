package com.example.spotifysdkimplementation;

public class WrappedClass {
    private String topArtist;
    private String topTrack;

    public WrappedClass(String topArtist, String topTrack) {
        this.topArtist = topArtist;
        this.topTrack = topTrack;
    }

    public String getTopArtist() {
        return topArtist;
    }

    public String getTopTrack() {
        return topTrack;
    }

}
