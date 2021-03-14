// blech tbh

import { pipe } from "../lib/fn.js";
import { map, filter, min } from "../lib/array.js";

const spells = {
    magicMissile: {
        cost: 53,
        cast: function ({ player, boss }) {
            return {
                player: { ...player },
                boss: update({ hitpoints: -4 })(boss),
            };
        },
    },
    drain: {
        cost: 73,
        cast: function ({ player, boss }) {
            return {
                player: update({ hitpoints: 2 })(player),
                boss: update({ hitpoints: -2 })(boss),
            };
        },
    },
    shield: {
        cost: 113,
        cast: function ({ player, boss }) {
            const shieldEffect = {
                name: "shield",
                apply: function ({ player, boss }) {
                    let newPlayer = { ...player };
                    if (this.duration === 6) {
                        newPlayer = update({ armor: 7 })(player);
                    }
                    if (this.duration === 1) {
                        newPlayer = update({ armor: -7 })(player);
                    }
                    return {
                        player: newPlayer,
                        boss: { ...boss },
                    };
                },
                duration: 6,
            };
            return {
                player: addEffect(shieldEffect)(player),
                boss,
            };
        },
    },
    poison: {
        cost: 173,
        cast: function ({ player, boss }) {
            const poisonEffect = {
                name: "poison",
                apply: ({ player, boss }) => {
                    return {
                        player: { ...player },
                        boss: update({ hitpoints: -3 })(boss),
                    };
                },
                duration: 6,
            };

            return {
                player: addEffect(poisonEffect)(player),
                boss,
            };
        },
    },
    recharge: {
        cost: 229,
        cast: function ({ player, boss }) {
            const rechargeEffect = {
                name: "recharge",
                apply: ({ player, boss }) => {
                    return {
                        player: update({ mana: 101 })(player),
                        boss: { ...boss },
                    };
                },
                duration: 5,
            };

            return {
                player: addEffect(rechargeEffect)(player),
                boss,
            };
        },
    },
};

example1();
example2();
part1();

function example1() {
    const player = {
        name: "Player",
        hitpoints: 10,
        armor: 0,
        mana: 250,
        effects: [],
    };
    const boss = {
        name: "Boss",
        hitpoints: 13,
        damage: 8,
    };

    console.log(iterate({player, boss}));
}

function example2() {
    const player = {
        name: "Player",
        hitpoints: 10,
        armor: 0,
        mana: 250,
        effects: [],
    };
    const boss = {
        name: "Boss",
        hitpoints: 14,
        damage: 8,
    };

    console.log(iterate({ player, boss }));
}

function part1() {
    const player = {
        name: "Player",
        hitpoints: 50,
        armor: 0,
        mana: 500,
        effects: [],
    };
    const boss = {
        name: "Boss",
        hitpoints: 58,
        damage: 9,
    };

    console.log(iterate({ player, boss }));
}

function iterate({ player, boss }) {
    const go = ({ gameHistories, minManaForWin }) => {
        if (gameHistories.length === 0) return minManaForWin;
        return go(playARound({ gameHistories, minManaForWin }));
    };

    return go({
        gameHistories: [[{ player, boss, totalMana: 0 }]],
        minManaForWin: 9999999,
    });
}

function playARound({ gameHistories, minManaForWin }) {
    return pipe(
        { gameHistories, minManaForWin },
        resolveEffectsAndFilter,
        playerCastSpellAndFilter,
        resolveEffectsAndFilter,
        bossAttackAndFilter
    );
}

function resolveEffectsAndFilter({ gameHistories, minManaForWin }) {
    const newHistories = gameHistories.map(resolveEffects);
    return removeCompleteGames({ gameHistories: newHistories, minManaForWin });
}

function resolveEffects(history) {
    const lastRound = history.slice(-1)[0];
    const updated = pipe(lastRound, applyEffects, decrementEffects);
    return history.concat({ ...updated, totalMana: lastRound.totalMana });
}

function playerCastSpellAndFilter({ gameHistories, minManaForWin }) {
    const newHistories = gameHistories.flatMap(playerCastSpell);
    return removeCompleteGames({ gameHistories: newHistories, minManaForWin });
}

function playerCastSpell(history) {
    const { player, boss, totalMana } = history.slice(-1)[0];

    const possibleSpells = Object.keys(spells)
        .filter(
            (spellName) =>
                !player.effects.map((effect) => effect.name).includes(spellName)
        )
        .map((spellName) => spells[spellName])
        .filter(({ cost }) => cost <= player.mana);

    return possibleSpells.map((spell) =>
        pipe(
            { player: update({ mana: -spell.cost })(player), boss },
            spell.cast,
            (nextRound) =>
                history.concat({
                    ...nextRound,
                    totalMana: totalMana + spell.cost,
                })
        )
    );
}

function bossAttackAndFilter({ gameHistories, minManaForWin }) {
    const newHistories = gameHistories.map(bossAttack);
    return removeCompleteGames({ gameHistories: newHistories, minManaForWin });
}

function bossAttack(history) {
    const { player, boss, totalMana } = history.slice(-1)[0];

    const attackValue = Math.max(boss.damage - player.armor, 1);
    return history.concat({
        player: update({ hitpoints: -attackValue })(player),
        boss: { ...boss },
        totalMana,
    });
}

function removeCompleteGames({ gameHistories, minManaForWin }) {
    const evaluatedHistories = gameHistories.map(evaluateHistory);

    const minManaForWinThisTurn = pipe(
        evaluatedHistories,
        map((history) => history.slice(-1)[0]),
        filter((lastRound) => lastRound.winner === "player"),
        map((playerWin) => playerWin.totalMana),
        min
    );

    const newMinManaForWin =
        minManaForWinThisTurn && minManaForWinThisTurn < minManaForWin
            ? minManaForWinThisTurn
            : minManaForWin;

    const unresolvedHistories = evaluatedHistories
        .filter((history) => !history.slice(-1)[0].winner)
        .filter((history) => history.slice(-1)[0].totalMana < newMinManaForWin);

    return {
        gameHistories: unresolvedHistories,
        minManaForWin: newMinManaForWin,
    };
}

function evaluateHistory(history) {
    const lastRound = history.slice(-1).map((round) => {
        const { player, boss } = round;
        if (player.hitpoints <= 0) return { ...round, winner: "boss" };
        if (boss.hitpoints <= 0) return { ...round, winner: "player" };
        return round;
    });
    const previousRounds = history.slice(0, -1);

    return previousRounds.concat(lastRound);
}

function applyEffects({ player, boss }) {
    return player.effects.reduce(
        ({ player, boss }, effect) => effect.apply({ player, boss }),
        { player, boss }
    );
}

function decrementEffects({ player, boss }) {
    return {
        player: {
            ...player,
            effects: player.effects
                .map((effect) => ({
                    ...effect,
                    duration: effect.duration - 1,
                }))
                .filter((effect) => effect.duration !== 0),
        },
        boss: {
            ...boss,
        },
    };
}

function update(delta) {
    return function (player) {
        const update = Object.keys(delta).reduce(
            (acc, key) => ({ ...acc, [key]: player[key] + delta[key] }),
            {}
        );

        return {
            ...player,
            ...update,
        };
    };
}

function addEffect(effect) {
    return function (player) {
        if (player.effects.map((effect) => effect.name).includes(effect.name))
            throw new Error(`Effect "${effect.name}" already applied`);

        return {
            ...player,
            effects: [...player.effects, effect],
        };
    };
}
