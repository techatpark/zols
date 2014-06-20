
var tag = document.createElement('script');

tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var player;
function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
        height: '365',
        width: '460',
        videoId: 'jebJ9itYTJE',
        events: {
            'onReady': onReady
        }
    });
}

function onReady() {
    
    player.addEventListener('onStateChange', function(e) {
        switch (e.data) {
            case YT.PlayerState.ENDED:
                $("#player").hide();
        }
    });
    
    $("#playVideo").on("click", function() {
        $("#player").show();
        player.playVideo();
    });
    
    
}
