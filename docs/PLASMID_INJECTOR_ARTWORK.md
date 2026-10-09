# Plasmid injector artwork

The twelve plasmid slots in `core/src/main/assets/items.png` use one injector
silhouette with their existing liquid colours. The icons occupy 64×64 atlas
cells, using a 16×16 logical grid enlarged four times with hard pixel edges.
Medigel Stims (`KoltoPod.Device`) use the jade variant and Adrenal Stims
(`AdrenalBoost.Device`) use the azure variant from the same atlas row. Forcefield
Stims (`WeakForcefield.Device`) use the magenta variant. All three previously
referenced separate graphics in the device row. Their
constructor image assignments also apply when items are restored from saves;
their effects and saved class names are unchanged.
The original atlas is preserved in
`sprite-backups/items-before-plasmid-injectors.png`.

## Source and installation

- `plasmid-injectors-source.png`: source sheet made with the built-in imagegen tool.
- `plasmid-injectors-preview.png`: preview of the installed twelve variants.
- `core/src/main/assets/plasmid_injectors.png`: installed icon strip.
- `scripts/InstallPlasmidInjectorArtwork.java`: fits the azure source icon and
  recolours its liquid chamber for all twelve slots, retaining a common shape.

Run from the repository root:

```sh
java scripts/InstallPlasmidInjectorArtwork.java docs/plasmid-injectors-source.png
```

## Generation prompt

Create a transparent 4-column, 3-row sheet of twelve matching sci-fi medical
injectors, replacing the reference vial icons. Colour order: crimson, amber,
golden, jade; turquoise, azure, indigo, magenta; bistre, charcoal, silver, ivory.
Use the same silhouette for every icon, changing only the liquid reservoir.
Angle each injector from a short metallic needle at lower left to a broad
thumb plunger at upper right. Use a silver/white body, charcoal outline,
finger grips and a saturated coloured liquid chamber. Match the green and blue
versions to every other version. Crisp low-resolution pixel art designed for
a 16×16 logical grid, with a 14×14 footprint and transparent margins.
No bottles, vials, jars, grenades, characters, labels, text, watermark or shadow.

In-game confirmation remains pending.
