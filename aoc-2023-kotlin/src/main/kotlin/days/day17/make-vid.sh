#! /usr/bin/env bash

ffmpeg -f image2 -framerate 50 \
  -i frame-%05d.png \
  -i ~/Soulseek\ Downloads/complete/lvghst/Super\ Mario\ Land/9-06\ Temple-Underground\ BGM.flac \
  -vf scale=iw*5:-1 \
  -sws_flags neighbor \
  -c:a libvorbis \
  -c:v libx264rgb \
  -crf 10 \
  -tune animation \
  -shortest \
  out.mkv
