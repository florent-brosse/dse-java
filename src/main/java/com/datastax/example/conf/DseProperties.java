package com.datastax.example.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ProtocolVersion;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Configuration
@ConfigurationProperties("dse")
@Validated
public class DseProperties {
	@NotNull
    private List<String> contactPoints;
    
    @NotBlank
    private String keyspace;
    
    @NotBlank
    private String localDC;
    
    private ConsistencyLevel consistencyLevel=ConsistencyLevel.ONE;
    
    private ProtocolVersion protocolVersion=ProtocolVersion.DSE_V1;
    
    @NotNull
    private Integer port;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String login;
    
    @NotBlank
    private String storename;
    
    private Integer readTimeout=3000;
    private Integer connectionTimeout=3000;
    private Integer constantSpeculativeExecutionPolicyDelay=500;
    private Integer constantSpeculativeExecutionPolicyMaxNumber=2;
    
	public List<String> getContactPoints() {
		return contactPoints;
	}
	public void setContactPoints(List<String> contactPoints) {
		this.contactPoints = contactPoints;
	}
	public String getLocalDC() {
		return localDC;
	}
	public void setLocalDC(String localDC) {
		this.localDC = localDC;
	}
	public ConsistencyLevel getConsistencyLevel() {
		return consistencyLevel;
	}
	public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
		this.consistencyLevel = consistencyLevel;
	}
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}
	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getStorename() {
		return storename;
	}
	public void setStorename(String storename) {
		this.storename = storename;
	}
	public Integer getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getConstantSpeculativeExecutionPolicyDelay() {
		return constantSpeculativeExecutionPolicyDelay;
	}
	public void setConstantSpeculativeExecutionPolicyDelay(Integer constantSpeculativeExecutionPolicyDelay) {
		this.constantSpeculativeExecutionPolicyDelay = constantSpeculativeExecutionPolicyDelay;
	}
	public Integer getConstantSpeculativeExecutionPolicyMaxNumber() {
		return constantSpeculativeExecutionPolicyMaxNumber;
	}
	public void setConstantSpeculativeExecutionPolicyMaxNumber(Integer constantSpeculativeExecutionPolicyMaxNumber) {
		this.constantSpeculativeExecutionPolicyMaxNumber = constantSpeculativeExecutionPolicyMaxNumber;
	}
	public String getKeyspace() {
		return keyspace;
	}
	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}
    
   // private String
}
