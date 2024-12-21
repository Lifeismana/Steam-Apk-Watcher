package bolts;

import bolts.Task;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class UnobservedErrorNotifier {
    private Task<?> task;

    public UnobservedErrorNotifier(Task<?> task) {
        this.task = task;
    }

    protected void finalize() throws Throwable {
        Task.UnobservedExceptionHandler unobservedExceptionHandler;
        try {
            Task<?> task = this.task;
            if (task != null && (unobservedExceptionHandler = Task.getUnobservedExceptionHandler()) != null) {
                unobservedExceptionHandler.unobservedException(task, new UnobservedTaskException(task.getError()));
            }
        } finally {
            super.finalize();
        }
    }

    public void setObserved() {
        this.task = null;
    }
}
