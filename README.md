![logo](./Immersive Aircraft x Immersive Engineering/logo.png)

让沉浸飞机使用沉浸工程的燃料。带有"iaie:fuel"标签的液体可以作为燃料，默认包括IE生物柴油，装了沉浸原油也可以使用柴油、含硫柴油和煤油。
燃料罐是带有"iaie:fuel_tanks"的物品，默认是沉浸金属桶。

使用方法：给燃料罐内加入燃油，然后放到飞机的燃料槽位里面。

配置文件会在 config/iaie-common.toml 中生成。将 disableSolidFuel 设为 false 即可让飞机重新接受煤炭、原木等固体燃料，同时你也可以用液体燃料。也可以更改biodieselFuelTick实现更改液体燃料燃烧的速度。

模组灵感来源于群峦航线。

Allow immersive aircrafts to use immersive engineering fuels. Fluids with tag "iaie:fuel" can be used as fuels. Default fuel include IE biodiesel. Diesel, sulfurized diesel and kerosene can also burn as fuels if immersive petroleum mod is installed.
Items with tag "iaie:fuel_tanks" can be used as fuel tanks. Default fuel tanks include IE metal barrel.

Way to use: add fuels to a fuel tank, then put the fuel tank into the fuel slot of a plane.

Config file will be generated in config/iaie-common.toml. Change "disableSolidFuel" into "false" can allow planes to use solid fuels. Meanwhile you can still use liquid fuels. The burning rate of liquid fuels can also be setted by changing "biodieselFuelTick" in config.

This mod is inspired by Firma Aircraft.