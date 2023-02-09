package com.glolearn.newbook.context;

public class UserContext {

    private static ThreadLocal<Long> currentMember = new ThreadLocal<>();

    private static ThreadLocal<Boolean> reissueFlag = new ThreadLocal<>();

    public static Long getCurrentMember(){
        return currentMember.get();
    }

    public static void setCurrentMember(Long memberId){
        currentMember.set(memberId);
    }

    public static void clear(){
        currentMember.remove();
        reissueFlag.remove();
    }

    public static Boolean getReissueFlag() {
        return reissueFlag.get();
    }

    public static void setReissueFlag(Boolean flag) {
        reissueFlag.set(flag);
    }

    public static void setUnAuthorized() {
        setCurrentMember(-1L);
    }

    public static boolean unAuthorized() {
        return getCurrentMember().longValue() < 0;
    }
}
