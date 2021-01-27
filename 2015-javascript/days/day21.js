import { pipe } from "../lib/fn.js";
import { sortBy, find } from "../lib/array.js";

const boss = {
    hitPoints: 109,
    damage: 8,
    armor: 2,
};

part1();
part2();

function part1() {
    return doIt(sortLowestCost, "player");
}

function part2() {
    return doIt(sortHighestCost, "boss");
}

function doIt(sortFn, desiredWinner) {
    return pipe(
        createShop(),
        generateOptions,
        generateInventories,
        evaluateInventories,
        sortBy(sortFn),
        find((inv) => {
            const player = { ...inv, hitPoints: 100 };
            return findWinner(player, boss) === desiredWinner;
        }),
        console.log
    );
}

function sortLowestCost(a, b) {
    return a.cost - b.cost;
}

function sortHighestCost(a, b) {
    return b.cost - a.cost;
}

function findWinner(player, boss) {
    const playerAttack = Math.max(player.damage - boss.armor, 1);
    const bossAttack = Math.max(boss.damage - player.armor, 1);

    const playerRoundsToZero = player.hitPoints / bossAttack;
    const bossRoundsToZero = boss.hitPoints / playerAttack;

    return playerRoundsToZero < bossRoundsToZero ? "boss" : "player";
}

function evaluateInventories(inventories) {
    return inventories.map((i) => ({
        inventory: [...i],
        cost: i.reduce((acc, current) => acc + current.cost, 0),
        damage: i.reduce((acc, current) => acc + current.damage, 0),
        armor: i.reduce((acc, current) => acc + current.armor, 0),
    }));
}

function generateInventories({ weaponOptions, armorOptions, ringOptions }) {
    return weaponOptions
        .flatMap((w) => armorOptions.map((a) => [...w, ...a]))
        .flatMap((wa) => ringOptions.map((r) => [...wa, ...r]));
}

function generateOptions({ weapons, armor, rings }) {
    const nothing = [{ item: "Nothing", cost: 0, damage: 0, armor: 0 }];

    const weaponOptions = weapons.map((w) => [w]);

    const armorOptions = [...armor.map((a) => [a]), nothing];

    console.log(armorOptions);

    const oneRing = rings.map((r) => [r]);
    const twoRings = rings.flatMap((r1, index) =>
        rings.slice(index + 1, rings.length).map((r2) => [r1, r2])
    );
    const ringOptions = [...twoRings, ...oneRing, nothing];

    return { weaponOptions, armorOptions, ringOptions };
}

function createShop() {
    const weapons = [
        "Dagger        8     4       0",
        "Shortsword   10     5       0",
        "Warhammer    25     6       0",
        "Longsword    40     7       0",
        "Greataxe     74     8       0",
    ];

    const armor = [
        "Leather      13     0       1",
        "Chainmail    31     0       2",
        "Splintmail   53     0       3",
        "Bandedmail   75     0       4",
        "Platemail   102     0       5",
    ];

    const rings = [
        "Damage +1    25     1       0",
        "Damage +2    50     2       0",
        "Damage +3   100     3       0",
        "Defense +1   20     0       1",
        "Defense +2   40     0       2",
        "Defense +3   80     0       3",
    ];

    return {
        weapons: weapons.map(parseItem),
        armor: armor.map(parseItem),
        rings: rings.map(parseItem),
    };
}

function parseItem(line) {
    const [, item, cost, damage, armor] = line.match(
        /(.+)  +(\d+) +(\d+) +(\d+)/
    );

    return {
        item: item.trim(),
        cost: parseInt(cost),
        damage: parseInt(damage),
        armor: parseInt(armor),
    };
}
