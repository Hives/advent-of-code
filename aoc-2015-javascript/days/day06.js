"use strict";
var __spreadArrays = (this && this.__spreadArrays) || function () {
    for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
    for (var r = Array(s), k = 0, i = 0; i < il; i++)
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
            r[k] = a[j];
    return r;
};
exports.__esModule = true;
var reader_js_1 = require("../lib/reader.js");
var array_js_1 = require("../lib/array.js");
var Operation;
(function (Operation) {
    Operation["turn on"] = "TurnOn";
    Operation["turn off"] = "TurnOff";
    Operation["toggle"] = "Toggle";
})(Operation || (Operation = {}));
var input = reader_js_1.readStrings("day06.txt");
function processInstruction(instructionString, rules) {
    var inst = parseInstruction(instructionString);
    for (var y = inst.start.y; y <= inst.end.y; y++) {
        for (var x = inst.start.x; x <= inst.end.x; x++) {
            rules[inst.operation]({ x: x, y: y });
        }
    }
}
function parseInstruction(instruction) {
    var _a = instruction.match(/^(.*) (\d+),(\d+) through (\d+),(\d+)$/), operation = _a[1], x1 = _a[2], y1 = _a[3], x2 = _a[4], y2 = _a[5];
    return {
        operation: Operation[operation],
        start: {
            x: parseInt(x1),
            y: parseInt(y1)
        },
        end: {
            x: parseInt(x2),
            y: parseInt(y2)
        }
    };
}
function sumGrid() {
    return array_js_1.sum(grid.flat());
}
var testInstructions = [
    "turn on 0,0 through 7,7",
    "turn off 3,5 through 7,7",
    "toggle 1,1 through 6,6",
    "toggle 4,3 through 5,5",
];
var exampleInstructions = [
    "turn on 0,0 through 999,999",
    "toggle 0,0 through 999,0",
    "turn off 499,499 through 500,500",
];
var rules1 = {
    TurnOn: function (pnt) { return (grid[pnt.y][pnt.x] = 1); },
    TurnOff: function (pnt) { return (grid[pnt.y][pnt.x] = 0); },
    Toggle: function (pnt) { return (grid[pnt.y][pnt.x] = (grid[pnt.y][pnt.x] + 1) % 2); }
};
var rules2 = {
    TurnOn: function (pnt) { return (grid[pnt.y][pnt.x] = grid[pnt.y][pnt.x] + 1); },
    TurnOff: function (pnt) {
        return (grid[pnt.y][pnt.x] = Math.max(grid[pnt.y][pnt.x] - 1, 0));
    },
    Toggle: function (pnt) { return (grid[pnt.y][pnt.x] = grid[pnt.y][pnt.x] + 2); }
};
var grid;
var gridSize = 1000;
grid = __spreadArrays(Array(gridSize)).map(function (_) { return Array(gridSize).fill(0); });
input.forEach(function (i) { return processInstruction(i, rules1); });
console.log(sumGrid() + " - should be 543903");
grid = __spreadArrays(Array(gridSize)).map(function (_) { return Array(gridSize).fill(0); });
input.forEach(function (i) { return processInstruction(i, rules2); });
console.log(sumGrid() + " - should be 14687245");
