var safariVedio = function(doc){
        function log(info) {
            console.log(info);
            // alert(info);
        }
        function forceSafariPlayAudio() {
        	doc.load(); // iOS 9   还需要额外的 load 一下, 否则直接 play 无效
        	doc.play(); // iOS 7/8 仅需要 play 一下
        }

        // 可以自动播放时正确的事件顺序是
        // loadstart
        // loadedmetadata
        // loadeddata
        // canplay
        // play
        // playing
        // 
        // 不能自动播放时触发的事件是
        // iPhone5  iOS 7.0.6 loadstart
        // iPhone6s iOS 9.1   loadstart -> loadedmetadata -> loadeddata -> canplay
        doc.addEventListener('loadstart', function() {
            log('loadstart');
        }, false);
        doc.addEventListener('loadeddata', function() {
            log('loadeddata');
        }, false);
        doc.addEventListener('loadedmetadata', function() {
            log('loadedmetadata');
        }, false);
        doc.addEventListener('canplay', function() {
            log('canplay');
        }, false);
        doc.addEventListener('play', function() {
            log('play');
            // 当 audio 能够播放后, 移除这个事件
            window.removeEventListener('touchstart', forceSafariPlayAudio, false);
        }, false);
        doc.addEventListener('playing', function() {
            log('playing');
        }, false);
        doc.addEventListener('pause', function() {
            log('pause');
        }, false);

        // 由于 iOS Safari 限制不允许 audio autoplay, 必须用户主动交互(例如 click)后才能播放 audio,
        // 因此我们通过一个用户交互事件来主动 play 一下 audio.
        window.addEventListener('touchstart', forceSafariPlayAudio, false);
}