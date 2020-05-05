package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author:wangrui
 * @Date:2020/5/1 16:26
 */
public class ImageServlet extends HttpServlet {
    /*
     * 功能描述:查看图片属性，既能查看所有，也能查看指定图片属性
     * @return void
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      //考虑到查看所有图片属性和查看指定图片属性
        //通过是否URL中带有参数来进行区分
        //存在imageId则查看指定图片属性，否则就查看所有图片属性
        //例如：URL/image？imageId=100，imageId的值就是“100”
        //如果imageId中不存在imageId，那么返回null
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            //查看所有图片属性
            selectAll(imageId,resp);
        }else{
            //查看指定图片
            selectOne(imageId,resp);
        }
    }
    private void selectAll(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1.创建一个ImageDao对象，并查找数据库
        ImageDao imageDao=new ImageDao();
        List<Image>images=imageDao.selectAll();
        //2.把查找到的结果转成JSON格式的字符串，并且写回给resp对象
        Gson gson=new GsonBuilder().create();
        //jsonData就是一个json格式的字符串了，就和之前约定的格式是一样的了
        //重点体会下面这行代码,这个方法的核心，gson帮忙自动完成了大量的格式转换工作
        //只要把之前的相关的字段都约定成统一的命名，下面的操作就可以一步到位的完成整个转换
        String jsonData=gson.toJson(images);
        resp.getWriter().write(jsonData);
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1.创建ImageDao对象
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        //2.使用gson把查到的数据转换成json格式，并写回给响应对象
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(image);
        resp.getWriter().write(jsonData);
    }

    /*
     * 功能描述:上传图片
     * @return void
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取图片的属性信息，并且存入数据库
        //1.1  需要创建一个factory对象，和upload对象，这是为了获取图片属性所做的准备工作
        //        固定的逻辑
        FileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
        //1.2 通过upload对象进一步解析请求（解析HTTP请求中奇怪的body中的内容）
        //     FileItem就代表一个上传的文件对象
        //     理论上来说，HTTP支持一个请求中同时上传多个文件
        List<FileItem>items=null;
        try {
            items=upload.parseRequest(req);
        } catch (FileUploadException e) {
            //出现异常说明解析错误！
            e.printStackTrace();
            resp.setContentType("application/json; charset=utf-8");
            //告诉客户端出现的具体的错误是啥
            resp.getWriter().write("{\"ok\":false,\"reason\":\"请求解析失败\"}");
            return;
        }
        //1.3 把FileItem中的属性提取出来。转换成Image对象，才能存到数据库中
        //      当前只考虑一张图片的情况
        FileItem  fileItem=items.get(0);
        Image image=new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int)fileItem.getSize());
        //手动获取当前日期，并转成格式化日期
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
        //自己构造一个路径来保存,引入一个时间戳是为了能够让文件路径能够唯一
        //image.setPath("./image/"+System.currentTimeMillis()+"_"+image.getImageName());
        // 计算md5
        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
        //修改文件路径的生成方法，使用MD5作为路径
        image.setPath("./image/"+image.getMd5());
        //存到数据库中
        ImageDao imageDao=new ImageDao();
        //看看数据库中是否存在相同的MD5值的图片，不存在，返回null
        Image existImage=imageDao.selectByMd5(image.getMd5());
        imageDao.insert(image);
        //2.获取图片内容信息，并且写入磁盘文件
        File file=new File(image.getPath());
        if (existImage==null) {
            try {
                fileItem.write(file);
            } catch (Exception e) {
                e.printStackTrace();
                resp.setContentType("application/json; charset=utf-8");
                resp.getWriter().write("{\"ok\":false,\"reason\":\"写磁盘失败\"}");
                return;
            }
        }
        //3.跟客户端返回一个结果数据
        //因为在实际上传图片完成后，显示ok：true，对于小白可能看不懂，因此需要在上传完成心得图片之后跳转到首页
       /* resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write("{\"ok\":true}");*/
        //重定向到首页
        resp.sendRedirect("index.html");
    }

    /*
     * 功能描述:删除指定图片
     * @return void
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       resp.setContentType("application/json;charset=utf-8");
        //1.先获取到请求中的imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"解析请求失败\"}");
            return;
        }
        //2.创建ImageDao对象，查看该图片对象对应的相关属性（这是为了知道这个图片对应的文件路径）
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        if(image==null){
            //此时请求中传入的id在数据库中不存在
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"imageId在数据库中不存在\"}");
            return;
        }
        //3.删除数据库记录
        imageDao.delete(Integer.parseInt(imageId));
        //4.删除本地磁盘文件
        File file=new File(image.getPath());
        file.delete();
        resp.setStatus(200);
        resp.getWriter().write("{\"ok\":true}");
        return;
    }
}
