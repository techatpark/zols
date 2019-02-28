export default function load() {
  const env = (process.env.NODE_ENV || "dev").toLowerCase();
  return {
    env,
    ...require(`./${env}.json`)
  };
}
