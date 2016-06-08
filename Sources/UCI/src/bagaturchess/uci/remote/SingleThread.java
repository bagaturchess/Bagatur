package bagaturchess.uci.remote;


/**
 *
 */
public class SingleThread extends Thread {

  private boolean mIsStoped;
  private Runnable mWork;
  private ThreadManager mManager;

  private Object mSyncWork = new Object(); //Sync object for critical mWork access.
  Object mSyncStart = new Object();


  public SingleThread( ThreadManager aManager ) {
    mIsStoped = false;
    mManager = aManager;
  }


  /**
   * Use this method only when the thread is already started!
   */
  public void setWork( Runnable aWork ) {
    synchronized( mSyncWork ) {
      if ( mWork != null ) {
        mWork = null;
        throw new IllegalStateException( "Single thread is busy!" );
      }
      mWork = aWork;
      mSyncWork.notifyAll();
    }
  }


  public void run() {
    while ( !mIsStoped ) {
      synchronized( mSyncWork ) {
        synchronized( mSyncStart ) {
          mSyncStart.notifyAll();
        }
        try {
          mSyncWork.wait();
        } catch (InterruptedException e) {
          throw new IllegalStateException( "Single thread is interrupted when waiting for work!" );
        }
        if ( mWork == null ) {
          throw new IllegalStateException( "Single thread is awaken but has no work!" );
        }
        mWork.run();
        mWork = null;
        mManager.returnToPool( this );
      }
    }
  }


  public void kill() {
    mIsStoped = true;
  }
}
