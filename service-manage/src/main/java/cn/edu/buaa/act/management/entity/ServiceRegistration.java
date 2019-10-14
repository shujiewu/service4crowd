package cn.edu.buaa.act.management.entity;

import cn.edu.buaa.act.management.common.ServiceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Id;

import javax.persistence.*;
import java.net.URI;
import java.util.Date;

/**
 * ServiceRegistration
 *
 * @author wsj
 * @date 2018/9/21
 */
@Getter
@Setter
@Entity
@Table(name = "service_registration")
public class ServiceRegistration implements Comparable<ServiceRegistration> {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //这个是hibernate的注解/生成32位UUID
    @GeneratedValue(generator="idGenerator")
    // @JsonIgnore  //在json序列化时将java bean中的一些属性忽略掉，序列化和反序列化都受影响。
    private String id;

    private String name;

    private String type;

    private String version;

    private String userId;

    private String permission;

    private String description;

    private Date createTime;
    private Date lastUpdatedTime;

    //存在mongodb中
    private String propertyId;

    @Lob
    private URI uri;

    @Lob
    private URI metaDataUri;

    private Boolean defaultVersion = false;

    public ServiceRegistration(){

    }
    public ServiceRegistration(String name, String type, String version, URI uri, URI metadataUri) {
        this.name = name;
        this.type=type;
        this.version =version;
        this.uri=uri;
        this.metaDataUri = metadataUri;
    }

    @Override
    public String toString() {
        return "ServiceRegistration{" + "name='" + name + '\'' + ", type='" + type + '\'' + ", version='" + version + '\'' + ", uri=" + uri + '}';
    }

    @Override
    public int compareTo(ServiceRegistration o) {
        int i = this.type.compareTo(o.type);
        if (i == 0) {
            i = this.name.compareTo(o.name);
        }
        if (i == 0) {
            i = this.version.compareTo(o.version);
        }
        return i;
    }
}
