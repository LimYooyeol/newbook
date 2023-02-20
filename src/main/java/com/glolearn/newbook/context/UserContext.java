package com.glolearn.newbook.context;

public class UserContext {

    private static ThreadLocal<Long> currentMember = new ThreadLocal<>();

    private static ThreadLocal<Boolean> reissueFlag = new ThreadLocal<>();

    /*
        #return :
            로그인 한 경우 -> member_id (token 에서 얻은 값)
            token 없는 경우 -> -1
     */
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
