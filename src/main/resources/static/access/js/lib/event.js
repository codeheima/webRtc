//url(http://www.w3school.com.cn/i/eg_bg_01.gif) repeat fixed
//  var videos = document.getElementById("videos");
//  var sendBtn = document.getElementById("sendBtn");
//  var msgs = document.getElementById("msgs");
//  var sendFileBtn = document.getElementById("sendFileBtn");
//  var files = document.getElementById("files");
	var rtc = chatRtc();
//
//  /**********************************************************/
//  sendBtn.onclick = function(event){
//    var msgIpt = document.getElementById("msgIpt"),
//        msg = msgIpt.value,
//        p = document.createElement("p");
//    p.innerText = "me: " + msg;
//    //广播消息
//    rtc.broadcast(msg);
//    msgIpt.value = "";
//    msgs.appendChild(p);
//  };
//
//  sendFileBtn.onclick = function(event){
//    //分享文件
//    rtc.shareFile("fileIpt");
//  };
  /**********************************************************/

  

  //对方同意接收文件
  rtc.on("send_file_accepted", function(sendId, socketId, file){
    var p = document.getElementById("sf-" + sendId);
    p.innerText = "对方接收" + file.name + "文件，等待发送";

  });
  //对方拒绝接收文件
  rtc.on("send_file_refused", function(sendId, socketId, file){
    var p = document.getElementById("sf-" + sendId);
    p.innerText = "对方拒绝接收" + file.name + "文件";
  });
  //请求发送文件
  rtc.on('send_file', function(sendId, socketId, file){
    var p = document.createElement("p");
    p.innerText = "请求发送" + file.name + "文件";
    p.id = "sf-" + sendId;
    files.appendChild(p);
  });
  //文件发送成功
  rtc.on('sended_file', function(sendId, socketId, file){
    var p = document.getElementById("sf-" + sendId);
    p.parentNode.removeChild(p);
  });
  //发送文件碎片
  rtc.on('send_file_chunk', function(sendId, socketId, percent, file){
    var p = document.getElementById("sf-" + sendId);
    p.innerText = file.name + "文件正在发送: " + Math.ceil(percent) + "%";
  });
  //接受文件碎片
  rtc.on('receive_file_chunk', function(sendId, socketId, fileName, percent){
    var p = document.getElementById("rf-" + sendId);
    p.innerText = "正在接收" + fileName + "文件：" +  Math.ceil(percent) + "%";
  });
  //接收到文件
  rtc.on('receive_file', function(sendId, socketId, name){
    var p = document.getElementById("rf-" + sendId);
    p.parentNode.removeChild(p);
  });
  //发送文件时出现错误
  rtc.on('send_file_error', function(error){
    console.log(error);
  });
  //接收文件时出现错误
  rtc.on('receive_file_error', function(error){
    console.log(error);
  });
  //接受到文件发送请求
  rtc.on('receive_file_ask', function(sendId, socketId, fileName, fileSize){
    var p;
    if (window.confirm(socketId + "用户想要给你传送" + fileName + "文件，大小" + fileSize + "KB,是否接受？")) {
      rtc.sendFileAccept(sendId);
      p = document.createElement("p");
      p.innerText = "准备接收" + fileName + "文件";
      p.id = "rf-" + sendId;
      files.appendChild(p);
    } else {
      rtc.sendFileRefuse(sendId);
    }
  });
  //成功创建WebSocket连接
  rtc.on("connected", function(socket) {
    //创建本地视频流
    rtc.createStream({
      "video": true,
      "audio": true
    });
    rtc.countTime();
  });
  //创建本地视频流成功
  rtc.on("stream_created", function(stream) {
	window.stream = stream
    document.querySelector(".BigVideo video").srcObject = stream;
    document.querySelector('.BigVideo video').play();
  });
  //创建本地视频流失败
  rtc.on("stream_create_error", function() {
    alert("create stream failed!");
  });
  //接收到其他用户的视频流
  rtc.on('pc_add_stream', function(stream, socketId) {
	var li = document.createElement("li");
	var liId = "li-"+socketId;
	li.innerHTML="<div class=\"content\">"+
								"<video id=\"audience-"+socketId+"\"></video>"+
                        "</div>"+
                        "<div class=\"join\">"+
                            "<i>"+
                                "<img src=\"/access/images/gmod.png\" alt=\"\">"+
                            "</i>"+
                        "</div>"+
                        "<i class=\"signals\">"+
                            "<img src=\"/access/images/signal.png\" alt=\"\">"+
                        "</i>"+
                        "<div class=\"control\">"+
                            "<span>大傻子</span>"+
                            "<i class=\"mic\">"+
                                "<img src=\"/access/images/mic.png\" alt=\"\">"+
                            "</i>"+
                            "<i class=\"gmod\">"+
                                "<img src=\"/access/images/gmod.png\" alt=\"\">"+
                            "</i>"+
                        "</div>";
	li.setAttribute("class","video-list");
	
	li.setAttribute("id",liId);
	
	document.querySelector(".audience ul").appendChild(li);
	
	var newVideo = document.getElementById("audience-"+socketId+"");
	var id = "audience-"+socketId+"";
//    var newVideo = document.createElement("video"),
//        id = "other-" + socketId;
    newVideo.setAttribute("class", "");
    //newVideo.setAttribute("autoplay", "autoplay");
    newVideo.setAttribute("playsinline","");
    newVideo.setAttribute("webkit-playsinline","")
    newVideo.setAttribute("x5-playsinline","");
    //videos.appendChild(newVideo);
    rtc.attachStream(stream, id);
  });
  //删除其他用户
  rtc.on('remove_peer', function(socketId) {
    var li = document.getElementById('li-' + socketId);
    if(li){
      li.parentNode.removeChild(li);
    }
  });
  //接收到文字信息
  rtc.on('data_channel_message', function(channel, socketId, message){
    var p = document.createElement("p");
    p.innerText = socketId + ": " + message;
    msgs.appendChild(p);
  });
  //连接WebSocket服务器
  //rtc.connect("wss:" + window.location.href.substring(window.location.protocol.length).split('#')[0], window.location.hash.slice(1));
  rtc.connect("wss://192.168.1.113:8443/roomChat", window.location.hash.slice(1));
  
  rtc.on("count_time",function(){
	  var span = document.querySelector(".time span")
	  var h=today.getHours()
	  var m=today.getMinutes()
	  var s=today.getSeconds()
	  h=checkTime(h)
	  m=checkTime(m)
	  s=checkTime(s)
	  document.getElementById('txt').innerHTML=h+":"+m+":"+s
	  t=setTimeout('startTime()',500)
	  
  })
  
  
  
  
  
  
  
  
  
  
  
  
  