package com.slykbots.muzika.lavastuff;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerDescriptor;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

// quick hack to fix soundcloud -> use the HTTP Audio URL instead of the Soundcloud (401) url
public class ScHttpAudioTrack extends HttpAudioTrack {
    public ScHttpAudioTrack(AudioTrackInfo trackInfo, AudioSourceManager sourceManager, MediaContainerDescriptor d, String title, String uploader) {
        // recreate track info with soundcloud title
        super(new AudioTrackInfo(title, uploader, trackInfo.length, trackInfo.identifier, trackInfo.isStream, trackInfo.uri, trackInfo.artworkUrl, trackInfo.isrc), d, (HttpAudioSourceManager) sourceManager);
    }
}