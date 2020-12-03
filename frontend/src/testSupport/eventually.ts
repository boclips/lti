export default async function eventually(assert: () => void) {
  let error: any;
  for (let i = 0; i < 10; i += 1) {
    try {
      assert();
      return;
    } catch (e) {
      error = e;
      // eslint-disable-next-line no-await-in-loop
      await new Promise((resolve) => setTimeout(resolve, i));
    }
  }
  throw new Error(error.message);
}
