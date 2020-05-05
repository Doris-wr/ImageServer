package api;

import dao.Image;
import dao.ImageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

/**
 * @Author:wangrui
 * @Date:2020/5/2 16:34
 */
public class ImageShowServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.解析出imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reason\":\"imageId解析失败\"}");
            return;
        }
        //2.根据imageId查找数据库，得到相应的图片属性信息（需要知道图片存储的路径）
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        //3.根据路径打开文件，读取其中的内容，写入到响应对象中
        OutputStream outputStream=resp.getOutputStream();
        resp.setContentType(image.getContentType());
        File file=new File(image.getPath());
        FileInputStream fileInputStream=new FileInputStream(file);//通过字节流对象读取数据
        byte[] buffer=new byte[1024];//缓冲区，使得读取效率更高
        while(true){
            int len=fileInputStream.read(buffer);//把数据读到buffer里
            if(len==-1){
                //文件读取结束
                break;
            }
            //此时已经读到一部分数据，放到buffer里，把buffer中的内容写到响应对象中
            outputStream.write(buffer);
        }
        fileInputStream.close();
        outputStream.close();
    }

    //增加防盗链
   /* static private HashSet<String> whiteList=new HashSet<>();
    static{
        whiteList.add(" http://localhost:9999/imageServer/index.html");
    }*/
    //实现防盗链
        /*String referer=req.getParameter("Referer");
        if(!whiteList.contains(referer)){
            resp.setContentType("application/json;charset:utf-8");
            resp.getWriter().write("{\"ok\":false,\"reason\":\"未授权的访问\"}");
            return;
        }*/
}
