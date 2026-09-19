# The Sleeper

The Security Block boss keeps the `MaskedPrisoner` class and `masked_prisoner.png` asset path for save compatibility, but appears in-game as **the Sleeper**, a century-old cryosleep prisoner. Y explains his history over three maze meetings. The third meeting applies Y's arena adjustment silently; Y only says, "Let's even the odds."

The new `masked_prisoner.png` is an 11-frame sprite strip with 56 x 64 physical-pixel frames. The first idle and attack poses are also saved separately in `art/sleeper/`. Built-in image generation used the previous boss strip and `guard.png` as scale/style references. The idle prompt requested a visible-faced, pale, disoriented human in a torn off-white cryo jumpsuit with broken restraints and cyan tubes, explicitly without a mask or assassin styling. A second prompt kept the same character design in a lunging attack pose. These were fitted with nearest-neighbour sampling; minor movement and death variants fill the existing animation layout.
