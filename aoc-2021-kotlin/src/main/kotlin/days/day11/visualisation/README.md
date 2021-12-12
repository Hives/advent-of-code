# Day 11 visualisation

Visualisation is here: https://www.youtube.com/watch?v=GsSI8O_w33Q

This visualisation uses a modified version of my solution code. I rendered 
the individual frames to `.png` files, and then combined them into a video 
with `ffmpeg`, thus:

```shell
ffmpeg -framerate 38 -i frame-%04d.png output.mp4
```

(Framerate of 38 was chosen to make the video length match the soundtrack 🙂)

A gif of an earlier version is here: https://imgur.com/vR9Js17
