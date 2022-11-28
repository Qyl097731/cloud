package com.nju.consts;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

/**
 * @description
 * @date:2022/11/27 19:59
 * @author: qyl
 */
public class SchemasConst {
    public static final StructType SCHEMA = new StructType ( )
            .add ("title", DataTypes.StringType, true)
            .add ("author", DataTypes.StringType, true)
            .add ("language", DataTypes.StringType, true)
            .add ("divider", DataTypes.StringType, true)
            .add ("watch", DataTypes.IntegerType, true)
            .add ("stars", DataTypes.IntegerType, true)
            .add ("fork", DataTypes.IntegerType, true)
            .add ("abstract", DataTypes.StringType, true)
            .add ("key_words", DataTypes.StringType, true)
            .add ("time", DataTypes.StringType, true);

    public static final StructType SOCIAL_SCHEMA = new StructType ( )
            .add ("title", DataTypes.StringType, true)
            .add ("watch", DataTypes.IntegerType, true)
            .add ("stars", DataTypes.IntegerType, true)
            .add ("fork", DataTypes.IntegerType, true);

}
