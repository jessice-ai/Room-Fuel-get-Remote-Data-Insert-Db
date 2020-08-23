package com.example.sun11.data.room

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
