package bagaturchess.uci.remote;


import java.util.ArrayList;


/**
 *
 */
public class ThreadManager {

  private static final int THREAD_POOL_INITIAL_SIZE = 100;
  private static int sThreadCount = 0;
  ArrayList mFreeThreads;


  public ThreadManager() {
    mFreeThreads = new ArrayList( THREAD_POOL_INITIAL_SIZE );
  }


  public synchronized void process( Runnable aWork ) {
    System.out.println( "Current thread pool size is " + sThreadCount );
    SingleThread freeThread = null;
    if ( mFreeThreads.size() > 0 ) {
      freeThread = ( SingleThread ) mFreeThreads.remove( 0 );
      System.out.println( "Thread is POOLED" );
    } else {
      System.out.println( "Thread is CREATED" );
      freeThread = new SingleThread( this );
      freeThread.start();
      sThreadCount++;
      synchronized( freeThread.mSyncStart ) {
        try {
          freeThread.mSyncStart.wait();
        } catch (InterruptedException e) {
          //Do nothing
        }
      }
    }
    freeThread.setWork( aWork );
    System.out.println( "New thread pool size is " + sThreadCount );
  }


  synchronized void returnToPool( SingleThread aFreeThread ) {
    mFreeThreads.add( aFreeThread );
  }
}
