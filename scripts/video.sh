#!/bin/sh

ffmpeg -i video.mp4 -i audio.mp3 -map 0:v -map 1:a -codec copy -shortest out.mp4

