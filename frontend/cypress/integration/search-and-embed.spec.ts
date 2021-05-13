context('LTI search and embed', () => {
  const endpoint = 'http://localhost:9000/search-and-embed.html';

  it('has a homepage', () => {
    cy.visit(`${endpoint}`);
    cy.wait(1000);
    cy.get('[data-qa="search-input"]').type('Minute');
    cy.get('[data-qa="search-button"]').click();

    cy.wait(1000);
    cy.get('button:contains("Add")').should('be.visible');
    cy.get('[data-boclips-player-initialised=true]').should('be.visible');
    cy.get('[data-boclips-player-initialised=true] button').should(
      'be.visible',
    );
  });
});
