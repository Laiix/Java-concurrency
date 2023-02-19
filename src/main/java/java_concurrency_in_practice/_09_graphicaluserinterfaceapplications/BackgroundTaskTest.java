package java_concurrency_in_practice._09_graphicaluserinterfaceapplications;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;

public class BackgroundTaskTest {

    private static final ExecutorService backGroundExec = Executors.newSingleThreadExecutor();
    
    public static void test(JLabel label) {
        JButton startButton = new JButton();
        JButton cancelButton = new JButton();
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                class CancelListener implements ActionListener {
                    BackgroundTask<?> task;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(task!=null) {
                            task.cancel(true);
                        }
                    }
                }
                final CancelListener listener = new CancelListener();
                listener.task = new BackgroundTask<Void>() {

                    @Override
                    protected Void compute() {
                        while(moreWork() && !isCancelled()) {
                            doSomeWork();
                        }
                        return null;
                    }

                    private void doSomeWork() {
                    }

                    private boolean moreWork() {
                        return false;
                    }

                    @Override
                    protected void onCompletion(Void value, Throwable thrown, boolean cancelled) {
                        cancelButton.removeActionListener(listener);
                        label.setText("done");
                    }
                    
                };
                cancelButton.addActionListener(listener);
                backGroundExec.execute(listener.task);
            }
        });
    }
}
