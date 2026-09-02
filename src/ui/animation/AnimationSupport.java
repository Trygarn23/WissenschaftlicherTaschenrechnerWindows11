package ui.animation;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.util.function.Consumer;

public final class AnimationSupport
{
    private static final int FRAME_DELAY_MS = 16;
    private static final String TIMER_KEY = "animation.timer";

    private AnimationSupport()
    {
    }

    public static Timer animate(int durationMs, Consumer<Double> frame, Runnable onDone)
    {
        int duration = Math.max(0, durationMs);
        if (duration == 0)
        {
            frame.accept(1.0);
            if (onDone != null)
            {
                onDone.run();
            }
            return new Timer(FRAME_DELAY_MS, null);
        }

        long startNanos = System.nanoTime();
        Timer timer = new Timer(FRAME_DELAY_MS, null);
        timer.addActionListener(e -> {
            double elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0;
            double progress = clamp(elapsedMs / duration);
            frame.accept(easeOut(progress));

            if (progress >= 1.0)
            {
                timer.stop();
                if (onDone != null)
                {
                    onDone.run();
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
        return timer;
    }

    public static void animateBackground(JComponent component, Color from, Color to, int durationMs)
    {
        stopComponentAnimation(component);
        Timer timer = animate(durationMs,
                progress -> {
                    component.setBackground(interpolateColor(from, to, progress));
                    component.repaint();
                },
                () -> {
                    component.setBackground(to);
                    component.repaint();
                });
        component.putClientProperty(TIMER_KEY, timer);
    }

    public static void pulseBackground(JComponent component, Color pulseColor, int durationMs)
    {
        Color original = component.getBackground();
        int half = Math.max(1, durationMs / 2);
        stopComponentAnimation(component);
        Timer first = animate(half,
                progress -> {
                    component.setBackground(interpolateColor(original, pulseColor, progress));
                    component.repaint();
                },
                () -> {
                    Timer second = animate(half,
                            progress -> {
                                component.setBackground(interpolateColor(pulseColor, original, progress));
                                component.repaint();
                            },
                            () -> {
                                component.setBackground(original);
                                component.repaint();
                            });
                    component.putClientProperty(TIMER_KEY, second);
                });
        component.putClientProperty(TIMER_KEY, first);
    }

    public static void stopComponentAnimation(JComponent component)
    {
        Object existing = component.getClientProperty(TIMER_KEY);
        if (existing instanceof Timer timer && timer.isRunning())
        {
            timer.stop();
        }
    }

    public static Color interpolateColor(Color from, Color to, double progress)
    {
        double p = clamp(progress);
        return new Color(
                interpolate(from.getRed(), to.getRed(), p),
                interpolate(from.getGreen(), to.getGreen(), p),
                interpolate(from.getBlue(), to.getBlue(), p),
                interpolate(from.getAlpha(), to.getAlpha(), p)
        );
    }

    static double easeOut(double progress)
    {
        double p = clamp(progress);
        return 1.0 - Math.pow(1.0 - p, 3.0);
    }

    private static int interpolate(int from, int to, double progress)
    {
        return (int) Math.round(from + (to - from) * progress);
    }

    private static double clamp(double value)
    {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
