# Bug Notes

## WaveBlaster Crash On Shoot

If WaveBlaster crashes immediately after firing, check `WaveBlaster.BlastWave` before changing projectile or knockback logic.

`Group.recycle()` instantiates visual effects reflectively. `BlastWave` must remain `public static`; if it is private, Android can throw an `IllegalAccessException`, `parent.recycle(BlastWave.class)` returns null, and `BlastWave.blast(...)` can crash with a null dereference.

The current fix is:

- `WaveBlaster.BlastWave` is `public static`.
- `BlastWave.blast(...)` checks whether `parent.recycle(...)` returned null before calling `reset(...)`.
