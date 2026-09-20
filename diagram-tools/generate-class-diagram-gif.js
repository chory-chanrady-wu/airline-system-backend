const fs = require('fs');
const path = require('path');
const sharp = require('sharp');
const { GIFEncoder, quantize, applyPalette } = require('gifenc');

const backendRoot = path.resolve(__dirname, '..');
const svgPath = path.join(backendRoot, 'CLASS_DIAGRAM_animated.svg');
const outputPath = path.join(backendRoot, 'CLASS_DIAGRAM_animated.gif');

const FPS = 12;
const TOTAL_SECONDS = 4.2;
const TOTAL_FRAMES = Math.ceil(FPS * TOTAL_SECONDS);
const FRAME_DELAY_MS = Math.round(1000 / FPS);
const OUTPUT_WIDTH = 1400;

function clamp(value, min, max) {
  return Math.max(min, Math.min(max, value));
}

function progressAt(timeSeconds, beginSeconds, durationSeconds) {
  return clamp((timeSeconds - beginSeconds) / durationSeconds, 0, 1);
}

function renderFrameSvg(sourceSvg, timeSeconds) {
  let svg = sourceSvg;

  svg = svg.replace(
    /<g class="fade-in"([^>]*)>\s*<animate attributeName="opacity"[^>]*begin="([\d.]+)s"[^>]*dur="([\d.]+)s"[^>]*\/>([\s\S]*?)<\/g>/g,
    (_, attributes, begin, dur, content) => {
      const opacity = progressAt(timeSeconds, parseFloat(begin), parseFloat(dur)).toFixed(3);
      return `<g class="fade-in"${attributes} opacity="${opacity}">${content}</g>`;
    }
  );

  svg = svg.replace(
    /<path class="link"([^>]*)stroke-dasharray="([\d.]+)" stroke-dashoffset="([\d.]+)"([^>]*)>\s*<animate attributeName="stroke-dashoffset"[^>]*begin="([\d.]+)s"[^>]*dur="([\d.]+)s"[^>]*\/>(?:\s*)<\/path>/g,
    (_, before, dashArray, initialOffset, after, begin, dur) => {
      const dash = parseFloat(dashArray);
      const offset = dash * (1 - progressAt(timeSeconds, parseFloat(begin), parseFloat(dur)));
      return `<path class="link"${before}stroke-dasharray="${dashArray}" stroke-dashoffset="${offset.toFixed(2)}"${after}></path>`;
    }
  );

  svg = svg.replace(/<animate[^>]*\/>/g, '');
  return svg;
}

async function svgToRgba(svgText) {
  const image = sharp(Buffer.from(svgText)).resize({ width: OUTPUT_WIDTH, withoutEnlargement: false });
  return image.ensureAlpha().raw().toBuffer({ resolveWithObject: true });
}

async function main() {
  if (!fs.existsSync(svgPath)) {
    throw new Error(`SVG source not found: ${svgPath}`);
  }

  const sourceSvg = fs.readFileSync(svgPath, 'utf8');
  const firstFrame = await svgToRgba(renderFrameSvg(sourceSvg, 0));
  const { width, height } = firstFrame.info;

  const gif = GIFEncoder();

  for (let index = 0; index < TOTAL_FRAMES; index += 1) {
    const timeSeconds = index / FPS;
    const frameSvg = renderFrameSvg(sourceSvg, timeSeconds);
    const { data, info } = await svgToRgba(frameSvg);

    if (info.width !== width || info.height !== height) {
      throw new Error(`Frame ${index} dimensions changed unexpectedly.`);
    }

    const palette = quantize(data, 256, { format: 'rgba4444' });
    const indexed = applyPalette(data, palette, { format: 'rgba4444' });
    gif.writeFrame(indexed, width, height, { palette, delay: FRAME_DELAY_MS });
  }

  gif.finish();
  fs.writeFileSync(outputPath, gif.bytes());

  const sizeKb = Math.round(fs.statSync(outputPath).size / 1024);
  console.log(`Animated GIF created: ${outputPath}`);
  console.log(`Dimensions: ${width}x${height}`);
  console.log(`Frames: ${TOTAL_FRAMES}`);
  console.log(`Approx size: ${sizeKb} KB`);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});

