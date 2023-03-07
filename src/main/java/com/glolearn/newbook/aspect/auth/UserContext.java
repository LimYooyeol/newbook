package com.glolearn.newbook.aspect.auth;

// ThreadLocal 을 통해 현재 사용자 정보 저장
public class UserContext {

    private static ThreadLocal<Long> currentMember = new ThreadLocal<>();

    private static ThreadLocal<Boolean> reissueFlag = new ThreadLocal<>();

    protected static void setCurrentMember(Long id){
        currentMember.set(id);
    }

    public static Long getCurrentMember(){
        return currentMember.get();
    }

    protected static void setReissueFlag(){
        reissueFlag.set(true);
    }

    public static boolean getReissueFlag(){
        return (reissueFlag.get() == null) ? false : reissueFlag.get();
    }

    protected static void clear(){
        currentMember.remove();
        reissueFlag.remove();
    }

}
