package com.example.sun11

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
