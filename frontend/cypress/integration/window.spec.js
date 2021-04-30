context('LTI search', () => {
  const endpoint = 'http://localhost:9000/search.html';

  it('has a homepage', () => {
    cy.visit(`${endpoint}/`);

  });
});
