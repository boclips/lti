context('LTI search', () => {
  const endpoint = 'http://localhost:9000/search.html';

  it('can search and filter results', () => {
    cy.visit(`${endpoint}`);
    cy.wait(1000);
    cy.get('[data-qa="search-input"]').type('Minute');
    cy.get('button').contains('Search').click();
    cy.wait(1000);
    cy.get('[data-boclips-player-initialised=true]').should('be.visible');
    cy.get('[data-boclips-player-initialised=true] button').should(
      'be.visible',
    );

    cy.get('[data-qa="video-card"]').should((videoCard) => {
      expect(videoCard.length).to.equal(2);
    });

    cy.get('div[id="Subject"]').click();
    cy.get('span').contains('Physics').click();
    cy.get('[data-qa="apply-button"]').click();
    cy.get('[data-qa="video-card"]').should((videoCard) => {
      expect(videoCard.length).to.equal(1);
    });
  });
});
