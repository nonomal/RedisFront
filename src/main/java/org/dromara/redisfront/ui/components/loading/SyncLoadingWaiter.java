package org.dromara.redisfront.ui.components.loading;

import cn.hutool.core.lang.Assert;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dromara.redisfront.commons.Fn;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
class SyncLoadingWaiter<T> extends SwingWorker<T, Object> {

    private final Timer timer;
    private final AtomicInteger count;
    private final SyncLoadingDialog syncLoadingDialog;
    private static final String TIMEOUT_MESSAGE_KEY = "LoadingDialog.loadInfoLabel.timeout.message";
    @Setter
    private Supplier<T> supplier;
    @Setter
    private BiConsumer<T, Exception> biConsumer;

    public SyncLoadingWaiter(SyncLoadingDialog syncLoadingDialog) {
        this.syncLoadingDialog = syncLoadingDialog;
        this.count = new AtomicInteger(0);
        this.timer = new Timer(1000, _ -> {
            if (count.get() < 100) {
                setProgress(count.incrementAndGet());
            } else {
                this.publish("timeout");
            }
        });
        this.addPropertyChangeListener(event -> {
            if (Fn.equal(event.getPropertyName(), "state")) {
                if (StateValue.STARTED == event.getNewValue()) {
                    this.timer.start();
                } else if (StateValue.DONE == event.getNewValue()) {
                    terminated();
                }
            } else if (Fn.equal(event.getPropertyName(), "progress")) {
                this.syncLoadingDialog.setProgressValue((Integer) event.getNewValue());
            }
        });
    }

    public void terminated() {
        this.cancel(true);
        this.timer.stop();
        this.syncLoadingDialog.setVisible(false);
        this.syncLoadingDialog.dispose();
    }

    @Override
    protected void done() {
        try {
            T object = this.get();
            this.biConsumer.accept(object, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof CancellationException) {
                return;
            }
            this.biConsumer.accept(null, e);
        }
    }

    @Override
    protected void process(List<Object> chunks) {
        if (chunks.isEmpty()) {
            return;
        }
        if (Fn.equal(chunks.getFirst(), "timeout")) {
            this.timer.stop();
            this.cancel(true);
            this.syncLoadingDialog.setMessageValue(syncLoadingDialog.$tr(TIMEOUT_MESSAGE_KEY));
        }
    }

    @Override
    protected T doInBackground() {
        Assert.notNull(supplier, "supplier must not be null");
        return supplier.get();
    }
}