package com.suneee.eas.common.config;

import com.suneee.eas.common.uploader.LocalFsUploaderHandler;
import com.suneee.eas.common.uploader.MinioUploaderHandler;
import com.suneee.eas.common.uploader.UploaderHandler;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @user 子华
 * @created 2018/8/2
 */
@Configuration
public class UploaderAutoConfigure {
    private String url;
    private Integer port;
    private String accessKey;
    private String secretKey;
    private String type;
    private Boolean isSecure;

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
        this.url=env.getProperty("uploader.minio.url");
        this.port=env.getProperty("uploader.minio.port",Integer.class);
        this.accessKey=env.getProperty("uploader.minio.accessKey");
        this.secretKey=env.getProperty("uploader.minio.secretKey");
        this.type=env.getProperty("uploader.type");
        this.isSecure=env.getProperty("uploader.minio.secure",Boolean.class,false);
    }

    /**
     * 注册minio客户端
     * @return
     * @throws InvalidPortException
     * @throws InvalidEndpointException
     */
    @Bean
    public MinioClient getMinioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(url, port, accessKey, secretKey,isSecure);
    }

    /**
     * 注册上传handler
     * @return
     */
    @Bean
    public UploaderHandler getUploaderHandler(){
        switch (type){
            case "minio":
                return new MinioUploaderHandler();
            default:
                return new LocalFsUploaderHandler();
        }
    }
}
