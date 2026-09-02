package ui.animation;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimationSupportTest
{
    @Test
    void interpolateColor_ShouldBlendChannels()
    {
        Color result = AnimationSupport.interpolateColor(new Color(0, 10, 20), new Color(100, 110, 120), 0.5);

        assertEquals(new Color(50, 60, 70), result);
    }

    @Test
    void animate_ShouldJumpToEndState_WhenDurationIsZero()
    {
        AtomicReference<Double> progress = new AtomicReference<>(0.0);
        AtomicBoolean done = new AtomicBoolean(false);

        javax.swing.Timer timer = AnimationSupport.animate(0, progress::set, () -> done.set(true));

        assertEquals(1.0, progress.get());
        assertTrue(done.get());
        assertFalse(timer.isRunning());
    }
}
