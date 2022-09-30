package simple_multi_thread;

public class Ch01_Basic {
    public static void main(String[] args) {
        /**
         * Thread的run方法覆盖的原因:
         * 1. start()方法会调用一个本地方法：private native void start();
         * 2. Thread类要调用本地方法，首先要向jvm注册，所以Thread类的开头便是
         *  public class Thread implements Runnable {
         *      //Make sure registerNatives is the first thing <clinit> does.
         *      private static native void registerNatives();
         * 	    static {
         * 		    registerNatives();
         *      }
         * 	    ...
         * 	    ..
         *  }
         *  3. 本地方法 registerNatives定义在Thread.c文件中，定义了各个操作系统都要用到的关于线程的公用数据和操作，代码如下：
         *      JNIEXPORT void JNICALL Java_Java_lang_Thread_registerNatives (JNIEnv *env, jclass cls){
         * 	        (*env)->RegisterNatives(env, cls, methods, ARRAY_LENGTH(methods));
         *      }
         *      static JNINativeMethod methods[] = {
         *          {"start0", "()V",(void *)&JVM_StartThread},
         *          {"stop0", "(" OBJ ")V", (void *)&JVM_StopThread},
         *          {"isAlive","()Z",(void *)&JVM_IsThreadAlive},
         *          {"suspend0","()V",(void *)&JVM_SuspendThread},
         *          {"resume0","()V",(void *)&JVM_ResumeThread},
         *          {"setPriority0","(I)V",(void *)&JVM_SetThreadPriority},
         *          {"yield", "()V",(void *)&JVM_Yield},
         *          {"sleep","(J)V",(void *)&JVM_Sleep},
         *          {"currentThread","()" THD,(void *)&JVM_CurrentThread},
         *          {"countStackFrames","()I",(void *)&JVM_CountStackFrames},
         *          {"interrupt0","()V",(void *)&JVM_Interrupt},
         *          {"isInterrupted","(Z)Z",(void *)&JVM_IsInterrupted},
         *          {"holdsLock","(" OBJ ")Z",(void *)&JVM_HoldsLock},
         *          {"getThreads","()[" THD,(void *)&JVM_GetAllThreads},
         *          {"dumpThreads","([" THD ")[[" STE, (void *)&JVM_DumpThreads},
         *      };
         *  4. 可以看出Java线程调用start的方法，实际上会调用到JVM_StartThread方法。在jvm.cpp中，有如下代码段：
         *          JVM_ENTRY(void, JVM_StartThread(JNIEnv* env, jobject jthread))
         *          …
         *          native_thread = new JavaThread(&thread_entry, sz);
         *          …
         *      这里JVM_ENTRY是一个宏，用来定义JVM_StartThread函数，可以看到函数内创建了真正的平台相关的本地线程，
         *      其线程函数是thread_entry，代码如下：
         *          static void thread_entry(JavaThread* thread, TRAPS) {
         * 	            HandleMark hm(THREAD);
         * 	            Handle obj(THREAD, thread->threadObj());
         * 	            JavaValue result(T_VOID);
         * 	            JavaCalls::call_virtual(&result,obj,
         * 	            KlassHandle(THREAD,SystemDictionary::Thread_klass()),
         * 	            vmSymbolHandles::run_method_name(),
         * 	            vmSymbolHandles::void_method_signature(),THREAD);
         *          }
         *  5. 可以看到调用了vmSymbolHandles::run_method_name方法，这是在vmSymbols.hpp用宏定义的：
         *      class vmSymbolHandles: AllStatic {
         *          …
         *           template(run_method_name,"run")
         *          …
         *      }
         *  即调用了Thread的run方法，过程： Thread#start() ------->JVM_StartThread------->thread_entry
         *                                                                                       |
         *                              Thread#run()<-----------------------------------------<--
         *
         *
         *  创建线程:
         *  一：
         *  默认情况下，我们通常不实例化Thread，而是实例化Thread的一个子类，因为Thread的run方法如下：
         *      @Override
         *      public void run() {
         *          if (target != null) {
         *              target.run();
         *          }
         *      }
         *     即默认情况下，target==null,故run方法什么也不会做，所以一种情况下，创建一个线程通过实例化Thread的子类，重写其run方法：
         */
        Thread thread = new Thread(){
            @Override
            public void run() {
                //do somethig
                System.out.println("create thread by subclass");
            }
        };
        thread.start();

        /**
         * 二：
         * 通过观察run方法可知，我们可以将Runnable target = null赋值达到可以让run方法执行我们需要完成的事情，
         * 即将实现Runnable接口的实现类作为参数传入Thread对象中：
         */
        Thread thread2 = new Thread(new Runnable(){
            @Override
            public void run() {
                //do somethig
                System.out.println("create thread by runnable");
            }
        });
        thread2.start();
        /**
         * 思考:
         * 伪代码：new Thread(Runnable.run1){run2}.start(); 看了源代码，思考会执行run1还是run2？
         *  覆盖了run方法，很显然会执行run2
         *
         * 效率:
         * 多线程并不一定会提高程序的运行效率，相反，可能还会降低程序的运行效率
         */
    }
}
