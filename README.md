# Room-Fuel-get-Remote-Data-Insert-Db
Kotlin 1.3.72 Android SDK 30.0.0 Jetpack 1.3.0 - 基础模块 - Room 2.2.5 （Entity Dao Database）Fuel 远程数据写入数据库.txt
一、ROOM 2.2.5 数据库

Room数据库的三个对象

1、Entity  : 数据库表创建，使用Entity注解将一个类变成数据库中的一张表结构。
2、DAO : 数据库操作类，读写，SQL语句等。
3、RoomDatabase : 用于创建数据库链接。内部包含DAO和Entity。


参考资料：https://zhuanlan.zhihu.com/p/94008969


二、为何用Room

Google强烈推荐我们使用Room而不是SQLite

1、最常见的使用场景缓存相关数据到本地，当设备不能访问网络，用户仍然能离线浏览内容。
2、在设备重新联网后，任何用户发起的内容修改都能同步到服务器。
3、由于Room会为我们解决这些问题，因此Google强烈推荐我们使用Room而不是SQLite

说明：
1、Room在SQlite上提供了一层抽象层，允许你流畅的访问SQLite数据库的全部功能。
2、当应用程序在处理大量的数据结构的时候，能从本地数据获得很大的帮助。

二、授权，并添加依赖

1、授权

1）、app/manifests/AndroidManifest.xml  #编辑此文件
2）、<application 上面增加下面一下

<uses-permission android:name="android.permission.INTERNET"> 
</uses-permission>

2、添加依赖

dependencies {
    /**
     * 添加Fuel依赖
     */
    implementation 'com.github.kittinunf.fuel:fuel-gson:2.2.3'
    implementation 'com.google.code.gson:gson:2.8.2'
}


二、ROOM 依赖 添加

1）、#头部添加

apply plugin: 'kotlin-kapt'

2）build.gradle #添加以下依赖


dependencies {
    /**
     * Room 依赖
     */
    def room_version = "2.2.5"
    implementation "androidx.room:room-runtime:$room_version"
    kapt "androidx.room:room-compiler:$room_version" // For Kotlin use kapt instead of annotationProcessor
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation "androidx.room:room-ktx:$room_version"
    // optional - RxJava support for Room
    implementation "androidx.room:room-rxjava2:$room_version"
    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation "androidx.room:room-guava:$room_version"
    // Test helpers
    testImplementation "androidx.room:room-testing:$room_version"
}

三、创建包名与结构

adapter
data
----livedata
----room

说明：包名必须小写


四、数据表创建，Room（ Entity ）

room\SunUser.kt #内容如下：

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


/**
 * SunUser 实体类
 * @Entity()
 * 也可以写成
 * @Entity(tableName = "SunUser")
 */

@Entity
data class SunUser (
    /**
     * 主键自动增加
     * 注意：主键，自动增加，不能使用 val 声明，可用 var
     */
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,

    @ColumnInfo(name="first_name")
    var firstName:String,

    @ColumnInfo(name="last_name")
    var lastName:String,

    @ColumnInfo(name="birthday")
    var birthday:String,
    @ColumnInfo(name="picture")
    var picture:String,
    /**
     * 如果不希望某个变量生成表中的属性列，可以使用注解 @Ignore
     */
    @Ignore
    var nationality:String

    /**
     * 说明：
     * 1、实体类中有不想存储的字段，可用@Ignore注解
     * 2、ColumnInfo 字段
     * 3、PrimaryKey 主键
     * 4、@ForeignKey 指定外键 详情，查看 Kotlin 1.3.72 Android SDK 30.0.0 Jetpack 1.3.0 - 基础模块 - Room 2.2.5 .txt
     */
){
    constructor() : this(1, "111", "111","", "", "")
}


说明：
1、Entity：指定数据库名
2、tableName：表名
3、ColumnInfo：指定列
4、PrimaryKey：主键
5、autoGenerate = true #主键自动增加
6、ForeignKey #指定外键




五、Room Dao 数据库操作类（ 读写，SQL语句等 ）

1、room\SunUserDao.kt #内容如下：

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * 操作数据库 - 增删改查
 * @Insert、@Delete、@Update 和 @Query
 *
 */
@Dao
interface SunUserDao {
    /**
     * 这里返回值是LiveData而不是 MutableLiveData，因为我们不想其他的类能修改它的值
     * 这里的 getAll() 必须指定返回的数据类型，否则报错
     */
    @Query("SELECT * FROM sunuser")
    fun getAll(): LiveData<List<SunUser>>

    @Query("SELECT * FROM sunuser WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): SunUser

    /**
     * 插入一条数据,我们直接定义一个方法并用 @Insert 注解
     * 参数：onConflict 介绍
     * 说明：当插入的数据已经存在时候的处理逻辑
     * onConflict 有3个值：
     * 1、OnConflictStrategy.REPLACE  #替换
     * 2、OnConflictStrategy.ABORT  #终止
     * 3、OnConflictStrategy.IGNORE #忽略，直接插入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(vararg users: SunUser)

    /**
     * 默认根据主键删除
     */
    @Delete
    fun delete(user: SunUser)
    /**
     * 自定义删除条件
     * 也可以写成一下结构
     */
    @Query("delete from sunuser where id = :id ")
    fun deleteUserById(id:Long)

    /**
     * 默认根据主键更新
     */
    @Update
    fun updateUserByUser(user: SunUser)
    /**
     * 自定义更新条件
     * 也可以写成下面结构
     */
    @Query("update sunuser set first_name = :first_name where id =  :id")
    fun update(id: Long, first_name: String)

    @Query("SELECT * FROM sunuser")
    fun loadAllUsers(): Array<SunUser>
}





六、Room Database （ 创建数据库，名字：sunuser.db ）

1、room\SunUserDataBase.kt #内容如下：

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * entities 映射 SunUser 实体类
 * version 指明当前数据库的版本号
 * companion object 单例模式，返回Database，以防止新建过多的实例造成内存的浪费
 */
@Database(version = 1, entities = [SunUser::class])  //生命实体类（Entity）的名字 User 为跟数据库的版本号
abstract class SunUserDataBase : RoomDatabase() {
    abstract fun getUserDao(): SunUserDao //创建DAO的抽象类
    companion object {
        private var INSTANCE: SunUserDataBase? = null  //创建单例
        fun getInstance(context: Context): SunUserDataBase? {
            if (INSTANCE == null) {
                /**
                 * Room.databaseBuilder(context,klass,name).build()来创建Database
                 * 第一个参数 context：上下文
                 * 第二个参数 为当前Database的class字节码文件
                 * 第三个参数为数据库名称
                 * allowMainThreadQueries() #加上这个方法是允许 Room 在主线程上操作，默认是拒绝的，因为操作数据库都还算是比较耗时的动作
                 * 测试的时候，可以加上，正式的时候去掉，直接去掉这个方法
                 */
                INSTANCE = Room.databaseBuilder(
                    context,
                    SunUserDataBase::class.java,
                    DATABASE_NAME
                ).allowMainThreadQueries().build()
                /**
                 * 数据库升级和降级
                 * 目的，是修改表结构，这很正常，经常会用到
                 * 比如：数据库从版本1升级到版本2，并在版本2上增加了age一列
                 * 使用 addMigrations 函数实现 数据升级跟降级，
                 * addMigrations 的 Migration 参数说明：
                 * 1、Migration 有两个参数，第一个参数：数据库老版本号；第二个参数，数据库新版本号
                 * 2、同时将 @Database注解中的version的值 修改为新数据库的版本号
                 * 3、database.execSQL 中包含 修改表的 SQL 语句
                 */
//                INSTANCE = Room.databaseBuilder(
//                    context,
//                    SunUserDataBase::class.java,
//                    DATABASE_NAME
//                ).addMigrations(object : Migration(1,2){
//                    override fun migrate(database: SupportSQLiteDatabase) {
//                        database.execSQL("ALTER TABLE user ADD age INTEGER Default 0 not null ")
//                    }
//
//                }).build()
            }
            /**
             * 返回数据库
             */
            return INSTANCE
        }
        fun destroyInstance() {
            INSTANCE = null
        }
        private const val DATABASE_NAME = "sunuser.db"
    }
}

注意： 
1、编译时会检查SQL语句是否正确
2、不要在主线程中进行数据库操作
3、RoomDatabase最好使用单例模式

七、Activity.kt #内容如下：


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sun11.data.room.SunUser
import com.example.sun11.data.room.SunUserDataBase
import com.github.kittinunf.fuel.Fuel

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userDao = SunUserDataBase.getInstance(this@MainActivity)?.getUserDao()
        /**
         * 获取远程数据，并写入数据库
         */
        getremotedata()
        /**
         * 这里循环读取数据库中的内容
         * 返回的数据类型 Array<SunUser>
         */
        var userDataArray = userDao?.loadAllUsers()
        var names_str = "";
        userDataArray?.let {
            for (sun in it) {
                println("Jessice - firstName："+sun.firstName+" lastName: "+sun.lastName+" birthday："+sun.birthday+" picture："+sun.picture)
            }
        }

    }
    /**
     * Fuel 使用 POST 方式获取远程数据，返回的是 Javabean，代码
     * 获取远程数据，并转化为数组
     */
    fun getremotedata(){
        val httpAsync = Fuel.get("https://pixabay.com/api/?key=17946669-543fe6c4c313739ab33b63515&q=yellow+flowers&image_type=photo&pretty=true")
                .responseObject(Animal.Deserializer()) {request,response,result ->
                    val(animals, err) = result   //Kotlin 写法
                    //val animals = result.component1() //java写法
                    if (animals != null) {
                        /**
                         * 把远程数据，写入数据库
                         */
                        val userDao = SunUserDataBase.getInstance(this@MainActivity)?.getUserDao()
                        var i=1
                        for (cursor in animals.hits){
                            val user = SunUser(i, cursor.user, "默认","默认", cursor.userImageURL, "默认")
                            if (userDao != null) {
                                userDao.insertData(user)
                            }
                            i=i+1
                        }
                    }
                }
    }
}




八、生成的数据库，跟数据表所在目录

D:\phpstudy_pro\android\sun3\app\build\generated\source\kapt\debug\com\example\sun3\data\room\
D:\phpstudy_pro\android\sun3\app\build\generated\source\kapt\debug\com\example\sun3\data\room\SunUserDao_Impl.java
D:\phpstudy_pro\android\sun3\app\build\generated\source\kapt\debug\com\example\sun3\data\room\SunUserDataBase_Impl.java



九、在build.gradle 文件中 defaultConfig{  ...  } 加入配置信息：


javaCompileOptions {
        annotationProcessorOptions {
            arguments = [
                    "room.schemaLocation":"$projectDir/schemas".toString(),
                    "room.incremental":"true",
                    "room.expandProjection":"true"]
        }
    }


说明：生成data创建的json信息，便于查看表的创建情况


十、使用Fuel 获取远程内容


1、AnimalRemote.kt：创建远程数据结构

data class AnimalRemote(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)

data class Hit(
    val comments: Int,
    val downloads: Int,
    val favorites: Int,
    val id: Int,
    val imageHeight: Int,
    val imageSize: Int,
    val imageWidth: Int,
    val largeImageURL: String,
    val likes: Int,
    val pageURL: String,
    val previewHeight: Int,
    val previewURL: String,
    val previewWidth: Int,
    val tags: String,
    val type: String,
    val user: String,
    val userImageURL: String,
    val user_id: Int,
    val views: Int,
    val webformatHeight: Int,
    val webformatURL: String,
    val webformatWidth: Int
)

说明：需快速生成，参考文档：Android Studio 4.0.5 SDK 30.0.0 JsonToKotlin插件安装，快速构建数据类（实体类）.txt

2、Animal.kt：文件内容如下：


import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Animal 作用：
 * 可以把 Fuel 远程获取的 JSON 数据 也就是 Javabean 代码，反序列化成为数组。所以此文件需要添加GSON依赖
 */
data class Animal(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
){
    class Deserializer : ResponseDeserializable<Animal> {
        override fun deserialize(content: String): Animal? =
            Gson().fromJson(content, Animal::class.java)
    }
}

说明：

val hits: List<Hit>,
val total: Int,
val totalHits: Int

只取这几个值，返回的 反序列化的数据结构中，也只有这几个值
