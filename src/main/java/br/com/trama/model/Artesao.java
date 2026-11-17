package br.com.trama.model;

import jakarta.persistence.*;

@Entity @Table(name="artesao")
public class Artesao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_artesao") private Long id;
 @Column(nullable=false,length=120) private String nome;
 @Column(nullable=false,unique=true,length=11) private String cpf;
 @Column(nullable=false,length=20) private String telefone;
 @Column(name="whatsapp",nullable=false,length=20) private String whatsapp;
 @Column(length=160) private String email;
 public Artesao() {}
 public Long getId(){return id;} public String getNome(){return nome;} public void setNome(String v){nome=v;}
 public String getCpf(){return cpf;} public void setCpf(String v){cpf=v.replaceAll("\\D","");}
 public String getTelefone(){return telefone;} public void setTelefone(String v){telefone=v;}
 public String getWhatsapp(){return whatsapp;} public void setWhatsapp(String v){whatsapp=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
}
