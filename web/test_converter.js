// Automated Test Suite for Web Simulator (web/index.html)
// Ensures 100% calculation parity between JavaScript web engine and Java engine.

const fs = require('fs');
const path = require('path');

// Read web/index.html and extract the script content
const htmlContent = fs.readFileSync(path.join(__dirname, 'index.html'), 'utf8');
const scriptMatch = htmlContent.match(/<script>([\s\S]*?)<\/script>/);

if (!scriptMatch) {
  console.error("Could not locate <script> block in web/index.html");
  process.exit(1);
}

// Sandbox execution of mathematical functions
const scriptBody = scriptMatch[1];

// Extract CATEGORIES and helper functions without triggering DOM references
const sandboxContext = {};
const fnCode = `
  ${scriptBody.substring(0, scriptBody.indexOf('// DOM Elements'))}
  sandboxContext.CATEGORIES = CATEGORIES;
  sandboxContext.convertTemperature = convertTemperature;
  sandboxContext.isBelowAbsoluteZero = isBelowAbsoluteZero;
  sandboxContext.convertUnits = convertUnits;
  sandboxContext.formatNumber = formatNumber;
  sandboxContext.getFormulaExplanation = getFormulaExplanation;
`;

const runInContext = new Function('sandboxContext', fnCode);
runInContext(sandboxContext);

const { CATEGORIES, convertTemperature, isBelowAbsoluteZero, convertUnits, formatNumber, getFormulaExplanation } = sandboxContext;

let testsPassed = 0;
let testsFailed = 0;
const DELTA = 1e-4;

function assertClose(testName, actual, expected, delta = DELTA) {
  if (Math.abs(actual - expected) <= delta) {
    console.log(`  [PASS] ${testName} -> ${actual}`);
    testsPassed++;
  } else {
    console.error(`  [FAIL] ${testName} -> Expected ${expected}, got ${actual}`);
    testsFailed++;
  }
}

function assertEqual(testName, actual, expected) {
  if (actual === expected) {
    console.log(`  [PASS] ${testName} -> "${actual}"`);
    testsPassed++;
  } else {
    console.error(`  [FAIL] ${testName} -> Expected "${expected}", got "${actual}"`);
    testsFailed++;
  }
}

function assertTrue(testName, condition) {
  if (condition) {
    console.log(`  [PASS] ${testName}`);
    testsPassed++;
  } else {
    console.error(`  [FAIL] ${testName} -> Expected true, got false`);
    testsFailed++;
  }
}

console.log("=========================================================");
console.log("   OIBSIP Unit Converter - Web Engine Parity Tests       ");
console.log("=========================================================");

// 1. Categories and Units count
console.log("\n1. Testing Category & Unit Registries:");
const catKeys = Object.keys(CATEGORIES);
assertEqual("6 Categories Registered", catKeys.length, 6);

let totalUnits = 0;
catKeys.forEach(k => {
  totalUnits += CATEGORIES[k].units.length;
});
assertEqual("32 Units Registered", totalUnits, 32);

// 2. Length Tests
console.log("\n2. Testing Length Conversions:");
const lenUnits = CATEGORIES.LENGTH.units;
const m = lenUnits.find(u => u.id === 'len_m');
const cm = lenUnits.find(u => u.id === 'len_cm');
const km = lenUnits.find(u => u.id === 'len_km');
const mi = lenUnits.find(u => u.id === 'len_mi');
assertClose("100 cm to m", convertUnits(100, cm, m, "LENGTH"), 1.0);
assertClose("1 km to m", convertUnits(1, km, m, "LENGTH"), 1000.0);
assertClose("1 mi to m", convertUnits(1, mi, m, "LENGTH"), 1609.344);

// 3. Weight Tests
console.log("\n3. Testing Weight Conversions:");
const wtUnits = CATEGORIES.WEIGHT.units;
const kg = wtUnits.find(u => u.id === 'wt_kg');
const g = wtUnits.find(u => u.id === 'wt_g');
const lb = wtUnits.find(u => u.id === 'wt_lb');
assertClose("1 kg to g", convertUnits(1, kg, g, "WEIGHT"), 1000.0);
assertClose("1 lb to kg", convertUnits(1, lb, kg, "WEIGHT"), 0.45359237);

// 4. Temperature Matrix
console.log("\n4. Testing Temperature Conversions:");
const tempUnits = CATEGORIES.TEMPERATURE.units;
const c = tempUnits.find(u => u.id === 'temp_c');
const f = tempUnits.find(u => u.id === 'temp_f');
const k = tempUnits.find(u => u.id === 'temp_k');
assertClose("0 °C to °F", convertUnits(0, c, f, "TEMPERATURE"), 32.0);
assertClose("100 °C to °F", convertUnits(100, c, f, "TEMPERATURE"), 212.0);
assertClose("-40 °C to °F", convertUnits(-40, c, f, "TEMPERATURE"), -40.0);
assertClose("0 °C to K", convertUnits(0, c, k, "TEMPERATURE"), 273.15);
assertClose("273.15 K to °C", convertUnits(273.15, k, c, "TEMPERATURE"), 0.0);
assertClose("32 °F to K", convertUnits(32, f, k, "TEMPERATURE"), 273.15);

// 5. Absolute Zero Check
console.log("\n5. Testing Absolute Zero Limits:");
assertTrue("-273.16 °C is below abs zero", isBelowAbsoluteZero(-273.16, "temp_c", "TEMPERATURE"));
assertTrue("-273.15 °C is valid", !isBelowAbsoluteZero(-273.15, "temp_c", "TEMPERATURE"));
assertTrue("-0.01 K is below abs zero", isBelowAbsoluteZero(-0.01, "temp_k", "TEMPERATURE"));
assertTrue("0 K is valid", !isBelowAbsoluteZero(0, "temp_k", "TEMPERATURE"));
assertTrue("-460 °F is below abs zero", isBelowAbsoluteZero(-460, "temp_f", "TEMPERATURE"));
assertTrue("-459.67 °F is valid", !isBelowAbsoluteZero(-459.67, "temp_f", "TEMPERATURE"));

// 6. Formatting & Formulas
console.log("\n6. Testing Formatting and Formulas:");
assertEqual("Format integer 100", formatNumber(100), "100");
assertEqual("Format integer 0", formatNumber(0), "0");
assertEqual("Format decimal 1.25", formatNumber(1.25), "1.25");
assertEqual("Formula C to F", getFormulaExplanation(0, c, f, 32, "TEMPERATURE"), "Formula: (°C × 9/5) + 32 = °F");
assertEqual("Formula 1 m = 100 cm", getFormulaExplanation(1, m, cm, 100, "LENGTH"), "1 m = 100 cm");

console.log("\n---------------------------------------------------------");
console.log(`Web Parity Summary: ${testsPassed} passed, ${testsFailed} failed.`);
console.log("=========================================================");

if (testsFailed > 0) {
  process.exit(1);
}
