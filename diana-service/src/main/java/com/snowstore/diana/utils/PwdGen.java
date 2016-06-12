package com.snowstore.diana.utils;

import java.util.Random;

/**
 * 密码生成器
 * @author wujinsong
 *
 */
public class PwdGen {
	private Random rdseed=new Random();
	  
    /**
     *@param
     *length        password length;
     *@param
     *letters       boolean non-capital letters combination control;
     *@param
     *letters_capi  boolean capital letters combination control;
     *@param
     *numbers       numbers control;
     */
    public  String getpwd(int length,boolean letters,boolean letters_capi,boolean numbers){
        StringBuilder sb=new StringBuilder();
        Random rd=this.rdseed;
        while(length-->0){
            char _1dgt=0;
            if(letters&&letters_capi&&numbers){
                int _key=rd.nextInt(3);
                if(2==_key){
                    _1dgt=get_L();
                }else if(1==_key){
                    _1dgt=get_L_C();
                }else if(0==_key){
                    _1dgt=get_N();
                }
            }else if(letters&&letters_capi&&!numbers){
                int _key=rd.nextInt(2);
                if(1==_key){
                    _1dgt=get_L();
                }else if(0==_key){
                    _1dgt=get_L_C();
                }
            }else if(!letters&&letters_capi&&numbers){
                int _key=rd.nextInt(2);
                if(1==_key){
                    _1dgt=get_N();
                }else if(0==_key){
                    _1dgt=get_L_C();
                }
            }else if(letters&&!letters_capi&&numbers){
                int _key=rd.nextInt(2);
                if(1==_key){
                    _1dgt=get_L();
                }else if(0==_key){
                    _1dgt=get_N();
                }
            }else if(letters&&!letters_capi&&!numbers){
                _1dgt=get_L();
            }else if(!letters&&!letters_capi&&numbers){
                _1dgt=get_N();
            }else if(!letters&&letters_capi&&!numbers){
                _1dgt=get_L_C();
            }else{
                break;
            }
             
            sb.append(_1dgt);
        }
        return sb.toString() ;
    }
    private char get_L_C(){
        Random rd=this.rdseed;
        int _dgt=(rd.nextInt(26)+65);
        return (char)_dgt; 
    }
    private char get_N(){
        Random rd=this.rdseed;
        int _dgt=(rd.nextInt(10)+48);
        return (char)_dgt;
    }
    private char get_L(){
        Random rd=this.rdseed;
        int _dgt=(rd.nextInt(26)+97);
        return (char)_dgt; 
    }
}
