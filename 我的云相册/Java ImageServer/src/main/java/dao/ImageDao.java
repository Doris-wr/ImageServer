package dao;
import commom.JavaImageServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:wangrui
 * @Date:2020/5/1 10:14
 */
public class ImageDao {
    /*
     * 功能描述:把image对象插入到数据库中
     * @return void
     */
    public void insert(Image image){
        //1.获取数据库连接
        Connection connection= null;
        PreparedStatement statement=null;
        try {
            connection = DBUtil.getConnection();
            //2.创建并拼装sql语句
            String sql="insert into image_table values(null,?,?,?,?,?,?)";
            statement=connection.prepareStatement(sql);
            statement.setString(1,image.getImageName());
            statement.setInt(2,image.getSize());
            statement.setString(3,image.getUploadTime());
            statement.setString(4,image.getContentType());
            statement.setString(5,image.getPath());
            statement.setString(6,image.getMd5());

            //3.执行sql语句
            int ret = statement.executeUpdate();
            if(ret!=1){
                //程序出现问题，抛出一个异常
                throw new JavaImageServerException("插入数据库异常");
            }
        } catch (SQLException | JavaImageServerException e) {
            e.printStackTrace();
        }
        //4.关闭连接和statement对象
        DBUtil.close(connection,statement,null);
    }

    /*
     * 功能描述:查找数据库中所有图片的信息
     * @return java.util.List<dao.Image>
     */
    public List<Image> selectAll(){
        List<Image> images=new ArrayList<>();
        //1.获取数据库连接
        Connection connection= null;
        //2.构造sql语句
        String sql="select* from image_table";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            connection = DBUtil.getConnection();
            //3.执行sql语句
            statement=connection.prepareStatement(sql);
            resultSet=statement.executeQuery();
            //4.处理结果集
            while(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                images.add(image);
            }
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    /*
     * 功能描述:根据imageId查找指定的图片信息
     * @return dao.Image
     */
    public Image selectOne(int imageId){
        //1.获取数据库连接
        Connection connection=DBUtil.getConnection();
        //2.构造sql语句
        String sql="select* from image_table where imageId=?";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            //3.执行sql语句
            statement=connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            resultSet = statement.executeQuery();
            //4.处理结果集
            if (resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    /*
     * 功能描述:根据imageId删除指定的图片
     * @return void
     */
    public void delete(int imageId){
        //1.获取数据库连接
        Connection connection=DBUtil.getConnection();
        //2.拼装sql语句
        String sql="delete from image_table where imageId=?";
        PreparedStatement statement=null;
        try {
            //3.执行sql语句
            statement=connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            int ret = statement.executeUpdate();
            if(ret!=1){
                throw new JavaImageServerException("删除数据库操作失败");
            }
        } catch (SQLException | JavaImageServerException e) {
            e.printStackTrace();
        }
        finally{
            //4.关闭连接
            DBUtil.close(connection,statement,null);
        }
    }
    //由于我的数据库是在阿里云服务器上，不在本地，这个程序在本地直接运行无法访问数据库
    //此处需要把这个程序部署到云服务器上执行才能看到效果
    //打一个jar包，把jar包拷贝到云服务器上就可以执行了
    public static void main(String[] args) {
        //用于进行简单的测试
        //1.测试插入数据
        /*Image image=new Image();
        image.setImageName("1.png");
        image.setSize(100);
        image.setUploadTime("20200501");
        image.setContentType("./data/1.png");
        image.setMd5("123324354");
        ImageDao imageDao=new ImageDao();
        imageDao.insert(image);*/
       //2.测试查找所有图片信息
        /*ImageDao imageDao=new ImageDao();
        List<Image>images=imageDao.selectAll();
        System.out.println(images );*/
        //3.测试查找指定图片信息
        /*ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(1);
        System.out.println(image);*/
        //4.测试删除照片
       /* ImageDao imageDao=new ImageDao();
        imageDao.delete(2)  ;*/
    }
    //按照md5查找
    public Image selectByMd5(String md5){
        //1.获取数据库连接
        Connection connection=DBUtil.getConnection();
        //2.构造sql语句
        String sql="select* from image_table where md5=?";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            //3.执行sql语句
            statement=connection.prepareStatement(sql);
            statement.setString(1,md5);
            resultSet = statement.executeQuery();
            //4.处理结果集
            if (resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

}
