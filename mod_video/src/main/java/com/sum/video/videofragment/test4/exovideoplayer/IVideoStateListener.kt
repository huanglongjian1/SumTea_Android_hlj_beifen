package com.sum.video.videofragment.test4.exovideoplayer

interface IVideoStateListener {
    fun onStateChange(state: State)
}

//视频状态
sealed interface State {
    //第一帧被渲染
    object FirstFrameRendered : State

    //缓冲结束，随时可播放。
    object Ready : State

    //播放出错
    class Error(val exception: Exception) : State
    class Idle(val currentPosition: Long):State

    //播放中
    object Playing : State

    //播放手动停止
    object Stop : State

    //播放结束
    object End : State

    //缓冲中
    object Buffering : State
}

interface VideoPlayer {
    // 视频url
    var url: String

    // 视频控制器，用于上层绘制进度条
    //  var playControl: MediaController.MediaPlayerControl

    // 视频状态回调
    var listener: IVideoStateListener?

    // 播放视频
    fun play()

    // 加载视频
    fun load()

    // 停止视频
    fun stop()

    // 释放资源
    fun release()
}