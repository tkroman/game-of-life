# [Conway's Game Of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)
![GOL glider](https://upload.wikimedia.org/wikipedia/commons/9/96/Animated_glider_emblem.gif)

## Implementation details
Built with Scala (3) & Scala.js, so can be run either in browser or in terminal.

Dependencies:
* [fusesource/jansi](https://github.com/fusesource/jansi/) to help with ANSI animations for terminal rendering
* [scala-js/scala-js-dom](https://github.com/scala-js/scala-js-dom) to manipulate DOM in the browser
* [monix/monix](https://github.com/monix/monix) to abstract over JVM/JS execution models

Most of the code in `shared` except for the actual platform-dependent renderers which are in `jvm`/`js` respectively.

I also did an [RLE](https://conwaylife.com/wiki/RLE) parser so both versions accept custom shape definitions.

## Running

### JVM

Syntax: `run width|21 height|21 frames-per-second|8 RLE|glider's RLE`

[Pulsar](https://conwaylife.com/wiki/Pulsar)

```shell
sbt 'lifeJVM/run 20 20 4 2b3o3b3o2b2$o4bobo4bo$o4bobo4bo$o4bobo4bo$2b3o3b3o2b2$2b3o3b3o2b$o4bobo4bo$o4bobo4bo$o4bobo4bo2$2b3o3b3o!'
```

### JS

Assemble JS: 
```shell
sbt 'lifeJS/fastLinkJS'
```

Arguments are passed via GET parameters: 
`file:///path/to/index.html?w=<WIDTH|21>&h=<HEIGHT|21>&fps=<FPS|8>&shape=<SHAPE_RLE|glider's RLE>`

## Improvement opportunities
I obviously got bored by the point it was reasonable to add error/broken input handling so negative grid dimensions, broken RLE etc will just blow up.

You can view this as either lazy programming or I can be preaching Erlang's Let It Crash, we'll never know.  

On a more serious note: 
* allowing to specify custom emojis/chars as cell skins
* making a UI for the JS version instead of passing stuff via GET parameters
* JS version kind of sucks wrt DOM usage efficiency since I'm just replacing the whole grid - might have been nicer if I reused the elements and replaced just the styles instead. Also, animation at 16+ FPS is too blinkey for my taste - maybe there are opportunities for improvement in that area as well.  
