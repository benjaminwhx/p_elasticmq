package com.github.elasticmq.sqs;

import com.amazonaws.regions.Regions;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 1:09 pm
 */
public class SqsConfig {
    public static final String DEFAULT_REGION = Regions.CN_NORTH_1.getName();
    private String region;
    private String endpoint;
    private String accessKey;
    private String secretKey;

    public SqsConfig() {
        this.region = DEFAULT_REGION;
        this.endpoint = null;
    }

    public SqsConfig(String accessKey, String secretKey) {
        this();
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public SqsConfig(String endpoint, String accessKey, String secretKey) {
        this(accessKey, secretKey);
        this.endpoint = endpoint;
    }

    public SqsConfig(Regions regions, String accessKey, String secretKey) {
        this(accessKey, secretKey);
        this.region = regions.getName();
    }

    public SqsConfig(Regions regions, String endpoint, String accessKey, String secretKey) {
        this(regions, accessKey, secretKey);
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return this.region;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(!(o instanceof SqsConfig)) {
            return false;
        } else {
            SqsConfig sqsConfig = (SqsConfig)o;
            if(this.region != null) {
                if(!this.region.equals(sqsConfig.region)) {
                    return false;
                }
            } else if(sqsConfig.region != null) {
                return false;
            }

            label45: {
                if(this.accessKey != null) {
                    if(this.accessKey.equals(sqsConfig.accessKey)) {
                        break label45;
                    }
                } else if(sqsConfig.accessKey == null) {
                    break label45;
                }

                return false;
            }

            if(this.secretKey != null) {
                if(!this.secretKey.equals(sqsConfig.secretKey)) {
                    return false;
                }
            } else if(sqsConfig.secretKey != null) {
                return false;
            }

            return this.endpoint != null?this.endpoint.equals(sqsConfig.endpoint):sqsConfig.endpoint == null;
        }
    }

    public int hashCode() {
        int result = this.region != null?this.region.hashCode():0;
        result = 31 * result + (this.accessKey != null?this.accessKey.hashCode():0);
        result = 31 * result + (this.secretKey != null?this.secretKey.hashCode():0);
        result = 31 * result + (this.endpoint != null?this.endpoint.hashCode():0);
        return result;
    }

}
