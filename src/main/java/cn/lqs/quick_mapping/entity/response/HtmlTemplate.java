package cn.lqs.quick_mapping.entity.response;

/**
 * 2022/10/4 10:03
 * created by @lqs
 */
public class HtmlTemplate {

    private final static String VIDEO_PAGE = """
            <!DOCTYPE html><html lang="cn"><head><meta charset="UTF-8"><meta http-equiv="X-UA-Compatible" content="IE=edge"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>${title}</title><style>html,body{ margin: 0; padding: 0; user-select: none; overflow: hidden; background-color: rgb(96, 96, 96);} #container{ margin: 25px; display: flex; justify-content: center; align-items: center;} video{ width: 100%; max-width: 1000px; border-radius: 25px; box-shadow: 2px 2px 2px solid salmon;} </style></head><body><div id="container"><video controls preload="metadata"><source src="${video-src}"><track label="Chinese" kind="captions" srclang="cn" src="" default></video></div><script>window.onload=function(){ document.querySelector('#container').style.height=window.innerHeight + 'px'} </script></body></html>
            """;

    private final static String NOT_SUPPORT_PAGE = """
            <!DOCTYPE html><html lang="cn"><head><meta charset="UTF-8"><meta http-equiv="X-UA-Compatible" content="IE=edge"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>无法解析的资源类型</title><style>h1{ margin: 35px auto; color: red; text-align: center;} p{ font-size: 16px; text-align: center; font-weight: bold; letter-spacing: 0.6px;} #time{ font-size: 14px;} </style></head><body><h1>错误 - 无法预览</h1><hr><p>当前的文件类型 <span style="color: blue">[${content-type}]</span>似乎无法被服务器解析.</p><p id="time"></p><script>window.onload=function(){ document.querySelector('#time') .innerHTML=new Date();}</script></body></html>
            """;

    private final static String IMG_PAGE = """
                <!DOCTYPE html><html lang="cn"><head><meta charset="UTF-8"><meta http-equiv="X-UA-Compatible" content="IE=edge"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>图片预览</title><style>html, body{ margin: 0; padding: 0; overflow: auto; background-color: rgba(96, 96, 96, 0.8);} #container{ width: 100%; display: flex; justify-content: center; align-items: center;} #container img{ width: 100%; max-width: 1000px; z-index: 9999; cursor: pointer;} </style></head><body><div id="container"><img id="img" title="单击图片放大-最大 12X" src="${img-url}" alt="IMG"></div><script>let room=1; function roomChange(){room +=1; if (room===13){ room=1};img.style=`transform: scale(${room})`;} window.onload=function (){ document.querySelector('#container').style.height=(window.innerHeight - 5) + 'px'; document.querySelector('#img').onclick=roomChange;} </script></body></html>        
            """;

    public static String renderVideoPage(String title, String videoUrl) {
        return VIDEO_PAGE.replaceFirst("\\$\\{title}", title)
                .replaceFirst("\\$\\{video-src}", videoUrl);
    }

    public static String renderNotSupportPage(String contentType) {
        return NOT_SUPPORT_PAGE.replaceFirst("\\$\\{content-type}", contentType);
    }

    public static String renderImgPage(String imgUrl){
        return IMG_PAGE.replaceFirst("\\$\\{img-url}", imgUrl);
    }
}
